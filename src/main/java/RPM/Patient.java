package RPM;

import AllVitalSigns.*;
import Report.*;
import Alarm.AlarmLevel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;

/**/

public class Patient {

    // Simulation engine that produces physiological values
    private VitalSignsGenerator generator;

    // 0 = normal simulation; 1 = abnormal simulation
    private int status;

    //Patient Details to be shown in the UI
    private int id;
    private String name;
    private int age;
    private String location;
    private String contact;

    /**
     * Generate and store time-series histories of each vital sign (HR, BP, RR, Temp, ECG)
     * Potential bug: Histories grow unbounded, may crash when the system runs for too long
     */
    private ArrayList<HeartRate> HeartRateHistory;
    private ArrayList<BloodPressure> BloodPressureHistory;
    private ArrayList<RespRate> RespRateHistory;
    private ArrayList<Temperature> TemperatureHistory;
    private ArrayList<ECG> ECGHistory; //Currently 1 sample per second in the simulation, in real life should be more frequent.

    /**
     * PermanentRecord writes minute averages and abnormal episodes into an Excel file.
     * It is continuously updated for this patient.
     */
    private final PermanentRecord permanentRecord;
    private final ArrayList<MinuteAverage> minuteAverages = new ArrayList<>();
    private final ArrayList<AbnormalEvent> abnormalEvents = new ArrayList<>();

    /**
     * Map of currently active abnormal episodes (one per vital type).
     * Key is the vital class name: "HeartRate", "RespRate", "Temperature", "BloodPressure", "ECG".
     */
    private final java.util.Map<String, AbnormalEvent> openEpisodes = new java.util.HashMap<>();

    // Per-minute running sums
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

        // Ensure the patient starts with at least one reading in each history
        updateVitals();
    }

    /**
     * Typical usage: called every 1000 ms by the UI timer.
     */
    public void updateVitals() {
        // Generates one new sample for each vital sign
        HeartRate hr = new HeartRate(generator.generateHeartRate());
        double sys = generator.generateSystolic();
        double dia = generator.generateDiastolic();
        BloodPressure bp = new BloodPressure(sys, dia);
        RespRate rr = new RespRate(generator.generateRespiratoryRate());
        Temperature temp = new Temperature(generator.generateBodyTemperature());
        ECG ecg = new ECG(generator.generateECG());

        // Appends to histories
        HeartRateHistory.add(hr);
        BloodPressureHistory.add(bp);
        RespRateHistory.add(rr);
        TemperatureHistory.add(temp);
        ECGHistory.add(ecg);

        // Abnormal event logging (warning + urgent)
        processAbnormalEpisode(hr);
        processAbnormalEpisode(rr);
        processAbnormalEpisode(temp);
        processAbnormalEpisode(bp);
        processAbnormalEpisode(ecg);

        // Minute average logging (every minute based on timestamps)
        updateMinuteAverages(hr, rr, temp, bp);
    }

    /**
     * Tracks abnormal "episodes" for each vital sign:
     * GREEN: close any open episode (if present)
     * AMBER & RED: start a new episode if none exists, or extend existing one
     * AMBER-RED transitions: close the old episode and open a new one
     *
     * Episodes are stored in openEpisodes until closed, then written to:
     * AbnormalEvents list (in-memory)
     * PermanentRecord Excel file (downloadable)
     */
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

        // AMBER&RED: open or extend episode
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

        // Same level: extend and update min/max value range + end time
        open.update(t, value);
    }

    // Closes an abnormal episode
    private void closeEpisode(String key, AbnormalEvent ev) {
        // Store in patient lists for daily report filtering
        abnormalEvents.add(ev);

        // Feed into permanent record file
        permanentRecord.appendAbnormalEvent(ev);

        // Remove from open episodes
        openEpisodes.remove(key);
    }

    /**
     * Closes all currently open abnormal episodes.
     * Intended to be called at shutdown or before generating a daily report.
     */
    public void finalizeOpenEpisodes() {
        for (String key : new java.util.ArrayList<>(openEpisodes.keySet())) {
            AbnormalEvent ev = openEpisodes.get(key);
            if (ev != null) closeEpisode(key, ev);
        }
    }

    /**
     * Flushes any pending minute average to storage and resets counters.
     * Intended to be called before daily report generation or at shutdown.
     */
    public void finalizeCurrentMinute() {
        flushMinuteAverage();
        // Prevent double-flush if called multiple times
        minuteCount = 0;
        sumHr = sumRr = sumTemp = sumSys = sumDia = 0;
    }


    private void updateMinuteAverages(HeartRate hr, RespRate rr, Temperature temp, BloodPressure bp) {

        // Bucket by minute
        LocalDateTime nowMinute = hr.getDateTime().truncatedTo(ChronoUnit.MINUTES);

        if (currentMinuteKey == null) {
            currentMinuteKey = nowMinute;
        }

        // If minute rolled over, flush previous minute if we have samples
        if (!nowMinute.equals(currentMinuteKey)) {
            flushMinuteAverage();
            // Reset for new minute
            currentMinuteKey = nowMinute;
            minuteCount = 0;
            sumHr = sumRr = sumTemp = sumSys = sumDia = 0;
        }

        // Accumulate current second’s values
        minuteCount++;
        sumHr += hr.getValue();
        sumRr += rr.getValue();
        sumTemp += temp.getValue();
        sumSys += bp.getSystole();
        sumDia += bp.getDiastole();
    }

    /**
     * Converts the current minute bucket's running sums into a MinuteAverage.
     * Stores it in memory, and feeds it to the permanent record file.
     */
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

    /**
     * Returns minute averages that fall on a specific LocalDate.
     * Used by the UI daily report generator.
     */
    public List<MinuteAverage> getMinuteAveragesForDate(LocalDate date) {
        ArrayList<MinuteAverage> out = new ArrayList<>();
        for (MinuteAverage m : minuteAverages) {
            if (m.getMinute().toLocalDate().equals(date)) out.add(m);
        }
        return out;
    }

    /**
     * Returns abnormal episodes that overlap a specified date.
     * Episodes can start before midnight and end after midnight, so we need to check overlap.
     */
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

    /* -------------------- Getters -------------------- */

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

    /**
     * Generic helper to slice the last N samples from a history list.
     * updateVitals() call corresponds to ~1 second of data.
     */
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
