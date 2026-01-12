package Report;

import Alarm.AlarmLevel;
import AllVitalSigns.BloodPressure;
import AllVitalSigns.HeartRate;
import AllVitalSigns.RespRate;
import AllVitalSigns.Temperature;
import RPM.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class PermanentRecord {

    // patientId -> (date -> dayRecord)
    private final Map<Integer, Map<LocalDate, DayRecord>> store = new HashMap<>();

    public synchronized void recordLatest(Patient p) {
        if (p == null) return;

        int pid = p.getId();
        LocalDate today = LocalDate.now();

        DayRecord day = store
                .computeIfAbsent(pid, k -> new HashMap<>())
                .computeIfAbsent(today, k -> new DayRecord());

        // record latest vitals (1 sample per second)
        HeartRate hr = p.getHr();
        RespRate rr = p.getRR();
        Temperature t = p.getTemp();
        BloodPressure bp = p.getBp();

        if (hr != null) day.recordHR(hr.getDateTime(), hr.getValue(), hr.getAlarmLevel());
        if (rr != null) day.recordRR(rr.getDateTime(), rr.getValue(), rr.getAlarmLevel());
        if (t != null)  day.recordTemp(t.getDateTime(), t.getValue(), t.getAlarmLevel());
        if (bp != null) day.recordBP(bp.getDateTime(), bp.getSystole(), bp.getDiastole(), bp.getAlarmLevel());
    }

    public synchronized List<MinuteAverage> getMinuteAverages(int patientId, LocalDate date) {
        DayRecord day = getDay(patientId, date);
        if (day == null) return Collections.emptyList();
        return day.buildMinuteAverages();
    }

    public synchronized List<AbnormalEvent> getAbnormalEvents(int patientId, LocalDate date) {
        DayRecord day = getDay(patientId, date);
        if (day == null) return Collections.emptyList();

        // already stored in chronological insertion order; but sort to be safe
        List<AbnormalEvent> copy = new ArrayList<>(day.abnormalEvents);
        copy.sort(Comparator.comparing(AbnormalEvent::getSecond));
        return copy;
    }

    private DayRecord getDay(int patientId, LocalDate date) {
        Map<LocalDate, DayRecord> byDate = store.get(patientId);
        if (byDate == null) return null;
        return byDate.get(date);
    }

    /* ===================== Internal Day Record ===================== */

    private static class DayRecord {

        // minute -> accumulator (keeps natural chronological order)
        private final Map<LocalDateTime, MinuteAccumulator> perMinute = new TreeMap<>();

        // every abnormal reading (AMBER/RED)
        private final List<AbnormalEvent> abnormalEvents = new ArrayList<>();

        void recordHR(LocalDateTime dt, double value, AlarmLevel level) {
            addToMinute(dt).addHR(value);
            recordAbnormal(dt, "HeartRate", value, level);
        }

        void recordRR(LocalDateTime dt, double value, AlarmLevel level) {
            addToMinute(dt).addRR(value);
            recordAbnormal(dt, "RespRate", value, level);
        }

        void recordTemp(LocalDateTime dt, double value, AlarmLevel level) {
            addToMinute(dt).addTemp(value);
            recordAbnormal(dt, "Temperature", value, level);
        }

        void recordBP(LocalDateTime dt, double sys, double dia, AlarmLevel level) {
            addToMinute(dt).addBP(sys, dia);
            // For minimal change: abnormal “value” column uses systolic only (matches your current report schema).
            recordAbnormal(dt, "BloodPressure", sys, level);
        }

        private void recordAbnormal(LocalDateTime dt, String type, double value, AlarmLevel level) {
            if (level == null || level == AlarmLevel.GREEN) return;
            abnormalEvents.add(new AbnormalEvent(dt, type, value, level));
        }

        private MinuteAccumulator addToMinute(LocalDateTime dt) {
            LocalDateTime minuteKey = dt.truncatedTo(ChronoUnit.MINUTES);
            return perMinute.computeIfAbsent(minuteKey, k -> new MinuteAccumulator());
        }

        List<MinuteAverage> buildMinuteAverages() {
            List<MinuteAverage> out = new ArrayList<>();
            for (Map.Entry<LocalDateTime, MinuteAccumulator> e : perMinute.entrySet()) {
                LocalDateTime minute = e.getKey();
                MinuteAccumulator acc = e.getValue();

                out.add(new MinuteAverage(
                        minute,
                        acc.avgHR(),
                        acc.avgRR(),
                        acc.avgTemp(),
                        acc.avgSys(),
                        acc.avgDia()
                ));
            }
            return out;
        }
    }

    /* ===================== Minute Accumulator ===================== */

    private static class MinuteAccumulator {
        private double hrSum = 0;   private int hrCount = 0;
        private double rrSum = 0;   private int rrCount = 0;
        private double tSum  = 0;   private int tCount  = 0;
        private double sysSum = 0;  private double diaSum = 0; private int bpCount = 0;

        void addHR(double v) { hrSum += v; hrCount++; }
        void addRR(double v) { rrSum += v; rrCount++; }
        void addTemp(double v) { tSum += v; tCount++; }
        void addBP(double sys, double dia) { sysSum += sys; diaSum += dia; bpCount++; }

        double avgHR() { return hrCount == 0 ? 0 : hrSum / hrCount; }
        double avgRR() { return rrCount == 0 ? 0 : rrSum / rrCount; }
        double avgTemp() { return tCount == 0 ? 0 : tSum / tCount; }
        double avgSys() { return bpCount == 0 ? 0 : sysSum / bpCount; }
        double avgDia() { return bpCount == 0 ? 0 : diaSum / bpCount; }
    }
}
