package RPM;

import AllVitalSigns.*;
import Report.*;
import Alarm.AlarmLevel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;

public class Patient {
    private VitalSignsGenerator generator;
    private int id;
    private String name;
    private int age;
    private String location;
    private String contact;
    private int status;

    //stores patient vital sign history
    private ArrayList<HeartRate> HeartRateHistory;
    private ArrayList<BloodPressure> BloodPressureHistory;
    private ArrayList<RespRate> RespRateHistory;
    private ArrayList<Temperature> TemperatureHistory;
    private ArrayList<ECG> ECGHistory;

    private final PermanentRecord permanentRecord;

    private final ArrayList<MinuteAverage> minuteAverages = new ArrayList<>();
    private final ArrayList<AbnormalEvent> abnormalEvents = new ArrayList<>();
    private final java.util.Map<String, AbnormalEvent> openEpisodes = new java.util.HashMap<>();

    // per-minute running sums
    private int minuteCount = 0;
    private double sumHr = 0, sumRr = 0, sumTemp = 0, sumSys = 0, sumDia = 0;
    private LocalDateTime currentMinuteKey = null;

    public Patient(int id, String name, int age, String location, String contact, int status) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.location = location;
        this.contact = contact;
        this.status = status;

        this.HeartRateHistory = new ArrayList<>();
        this.BloodPressureHistory = new ArrayList<>();
        this.RespRateHistory = new ArrayList<>();
        this.TemperatureHistory = new ArrayList<>();
        this.ECGHistory = new ArrayList<>();

        this.generator = new VitalSignsGenerator(status);
        this.permanentRecord = new PermanentRecord(this.name);
        updateVitals();
    }

    public void updateVitals() {
        HeartRate hr = new HeartRate(generator.generateHeartRate());
        double sys = generator.generateSystolic();
        double dia = generator.generateDiastolic();
        BloodPressure bp = new BloodPressure(sys, dia);
        RespRate rr = new RespRate(generator.generateRespiratoryRate());
        Temperature temp = new Temperature(generator.generateBodyTemperature());
        ECG ecg = new ECG(generator.generateECG());

        HeartRateHistory.add(hr);
        BloodPressureHistory.add(bp);
        RespRateHistory.add(rr);
        TemperatureHistory.add(temp);
        ECGHistory.add(ecg);

        // 1) abnormal event logging (warning + urgent)
        processAbnormalEpisode(hr);
        processAbnormalEpisode(rr);
        processAbnormalEpisode(temp);
        processAbnormalEpisode(bp);
        processAbnormalEpisode(ecg);

        // 2) minute average logging (every minute based on timestamps)
        updateMinuteAverages(hr, rr, temp, bp);
    }

    private void processAbnormalEpisode(AllVitalSigns.VitalSign v) {
        String key = v.getClass().getSimpleName();
        AlarmLevel level = v.getAlarmLevel();
        LocalDateTime t = v.getDateTime();
        double value = v.getValue();

        AbnormalEvent open = openEpisodes.get(key);

        // If GREEN: close any open episode
        if (level == AlarmLevel.GREEN) {
            if (open != null) {
                closeEpisode(key, open);
            }
            return;
        }

        // AMBER/RED: open or extend episode
        if (open == null) {
            // start new episode
            AbnormalEvent ev = new AbnormalEvent(t, key, value, level);
            openEpisodes.put(key, ev);
            return;
        }

        // If level changed (AMBER<->RED), close previous and start new
        if (open.getLevel() != level) {
            closeEpisode(key, open);
            AbnormalEvent ev = new AbnormalEvent(t, key, value, level);
            openEpisodes.put(key, ev);
            return;
        }

        // Same level: extend and update min/max
        open.update(t, value);
    }

    private void closeEpisode(String key, AbnormalEvent ev) {
        // store in patient lists for daily report filtering
        abnormalEvents.add(ev);

        // persist into permanent record file
        permanentRecord.appendAbnormalEvent(ev);

        // remove from open episodes
        openEpisodes.remove(key);
    }

    public void finalizeOpenEpisodes() {
        for (String key : new java.util.ArrayList<>(openEpisodes.keySet())) {
            AbnormalEvent ev = openEpisodes.get(key);
            if (ev != null) closeEpisode(key, ev);
        }
    }

    public void finalizeCurrentMinute() {
        flushMinuteAverage();
        // prevent double-flush if called multiple times
        minuteCount = 0;
        sumHr = sumRr = sumTemp = sumSys = sumDia = 0;
    }


    private void updateMinuteAverages(HeartRate hr, RespRate rr, Temperature temp, BloodPressure bp) {

        // bucket by minute
        LocalDateTime nowMinute = hr.getDateTime().truncatedTo(ChronoUnit.MINUTES);

        if (currentMinuteKey == null) {
            currentMinuteKey = nowMinute;
        }

        // if minute rolled over, flush previous minute if we have samples
        if (!nowMinute.equals(currentMinuteKey)) {
            flushMinuteAverage();
            // reset for new minute
            currentMinuteKey = nowMinute;
            minuteCount = 0;
            sumHr = sumRr = sumTemp = sumSys = sumDia = 0;
        }

        // accumulate current second’s values
        minuteCount++;
        sumHr += hr.getValue();
        sumRr += rr.getValue();
        sumTemp += temp.getValue();
        sumSys += bp.getSystole();
        sumDia += bp.getDiastole();

        // If you want strict "every minute" even if timestamps don’t roll
        // you can also flush when minuteCount == 60, but truncTo(MINUTES) is cleaner.
    }

    private void flushMinuteAverage() {
        if (minuteCount <= 0) return;

        MinuteAverage ma = new MinuteAverage(
                currentMinuteKey,
                sumHr / minuteCount,
                sumRr / minuteCount,
                sumTemp / minuteCount,
                sumSys / minuteCount,
                sumDia / minuteCount
        );

        minuteAverages.add(ma);
        permanentRecord.appendMinuteAverage(ma);
    }

    public List<MinuteAverage> getMinuteAveragesForDate(LocalDate date) {
        ArrayList<MinuteAverage> out = new ArrayList<>();
        for (MinuteAverage m : minuteAverages) {
            if (m.getMinute().toLocalDate().equals(date)) out.add(m);
        }
        return out;
    }

    public List<AbnormalEvent> getAbnormalEventsForDate(LocalDate date) {
        ArrayList<AbnormalEvent> out = new ArrayList<>();
        for (AbnormalEvent e : abnormalEvents) {
            LocalDate startD = e.getStart().toLocalDate();
            LocalDate endD = e.getEnd().toLocalDate();

            boolean overlaps = (!date.isBefore(startD)) && (!date.isAfter(endD));
            if (overlaps) out.add(e);
        }
        return out;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getLocation() { return location; }
    public String getContact() { return contact; }
    public int getStatus() { return status; }

    public HeartRate getHr() { return HeartRateHistory.get(HeartRateHistory.size() - 1); }
    public BloodPressure getBp() { return BloodPressureHistory.get(BloodPressureHistory.size() - 1); }
    public RespRate getRR() { return RespRateHistory.get(RespRateHistory.size() - 1); }
    public Temperature getTemp() { return TemperatureHistory.get(TemperatureHistory.size() - 1); }
    public ECG getECG() { return ECGHistory.get(ECGHistory.size() - 1); }

    public ArrayList<ECG> getECGHistory() { return ECGHistory; }
    public ArrayList<BloodPressure> getBloodPressureHistory() { return BloodPressureHistory; }
    public ArrayList<HeartRate> getHeartRateHistory() { return HeartRateHistory; }
    public ArrayList<RespRate> getRespRateHistory() { return RespRateHistory; }
    public ArrayList<Temperature> getTemperatureHistory() { return TemperatureHistory; }

    public PermanentRecord getPermanentRecord() { return permanentRecord; }

    private <T> ArrayList<T> genArray(ArrayList<T> history, int sec) {
        ArrayList<T> result = new ArrayList<>();
        if (history == null || history.isEmpty() || sec <= 0) return result;

        int size = history.size();
        int startIndex = Math.max(0, size - sec);
        for (int i = startIndex; i < size; i++) result.add(history.get(i));
        return result;
    }

    public ArrayList<HeartRate> getHrArr(int sec) { return genArray(HeartRateHistory, sec); }
    public ArrayList<BloodPressure> getBpArr(int sec) { return genArray(BloodPressureHistory, sec); }
    public ArrayList<RespRate> getRrArr(int sec) { return genArray(RespRateHistory, sec); }
    public ArrayList<Temperature> getTempArr(int sec) { return genArray(TemperatureHistory, sec); }
}
