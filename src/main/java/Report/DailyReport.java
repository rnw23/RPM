package Report;

import Alarm.AlarmLevel;
import AllVitalSigns.*;
import RPM.Patient;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class DailyReport {

    private final List<MinuteAverage> minuteAverages = new ArrayList<>();
    private final List<AbnormalEvent> abnormalEvents = new ArrayList<>();

    public DailyReport(Patient patient) {
        buildMinuteAverages(patient);
        buildAbnormalEvents(patient);
    }

    /* -------------------- Sheet 1: Vital signs average per minute -------------------- */

    private void buildMinuteAverages(Patient patient) {

        Map<LocalDateTime, MinuteAccumulator> perMinute = new TreeMap<>();

        addHeartRate(perMinute, patient.getHeartRateHistory());
        addRespRate(perMinute, patient.getRespRateHistory());
        addTemperature(perMinute, patient.getTemperatureHistory());
        addBloodPressure(perMinute, patient.getBloodPressureHistory());

        for (Map.Entry<LocalDateTime, MinuteAccumulator> entry : perMinute.entrySet()) {
            LocalDateTime minute = entry.getKey();
            MinuteAccumulator acc = entry.getValue();

            minuteAverages.add(new MinuteAverage(
                    minute,
                    acc.avgHR(),
                    acc.avgRR(),
                    acc.avgTemp(),
                    acc.avgSys(),
                    acc.avgDia()
            ));
        }
    }

    private void addHeartRate(Map<LocalDateTime, MinuteAccumulator> perMinute, List<HeartRate> history) {
        for (HeartRate v : history) {
            LocalDateTime key = v.getDateTime().truncatedTo(ChronoUnit.MINUTES);
            perMinute.computeIfAbsent(key, k -> new MinuteAccumulator()).addHR(v.getValue());
        }
    }

    private void addRespRate(Map<LocalDateTime, MinuteAccumulator> perMinute, List<RespRate> history) {
        for (RespRate v : history) {
            LocalDateTime key = v.getDateTime().truncatedTo(ChronoUnit.MINUTES);
            perMinute.computeIfAbsent(key, k -> new MinuteAccumulator()).addRR(v.getValue());
        }
    }

    private void addTemperature(Map<LocalDateTime, MinuteAccumulator> perMinute, List<Temperature> history) {
        for (Temperature v : history) {
            LocalDateTime key = v.getDateTime().truncatedTo(ChronoUnit.MINUTES);
            perMinute.computeIfAbsent(key, k -> new MinuteAccumulator()).addTemp(v.getValue());
        }
    }

    private void addBloodPressure(Map<LocalDateTime, MinuteAccumulator> perMinute, List<BloodPressure> history) {
        for (BloodPressure v : history) {
            LocalDateTime key = v.getDateTime().truncatedTo(ChronoUnit.MINUTES);
            perMinute.computeIfAbsent(key, k -> new MinuteAccumulator()).addBP(v.getSystole(), v.getDiastole());
        }
    }

    /* -------------------- Sheet 2: Abnormal events (time-based, chronological) -------------------- */

    private void buildAbnormalEvents(Patient patient) {

        // record only when a vital's alarm level changes into AMBER/RED (concise)
        Map<String, AlarmLevel> lastLevelByVital = new HashMap<>();

        List<VitalSign> all = new ArrayList<>();
        all.addAll(patient.getHeartRateHistory());
        all.addAll(patient.getRespRateHistory());
        all.addAll(patient.getTemperatureHistory());
        all.addAll(patient.getBloodPressureHistory());

        // time-based ordering
        all.sort(Comparator.comparing(VitalSign::getDateTime));

        for (VitalSign v : all) {

            AlarmLevel level = v.getAlarmLevel();
            String vitalType = v.getClass().getSimpleName();
            AlarmLevel prev = lastLevelByVital.get(vitalType);

            // GREEN: update state only, do not log
            if (level == AlarmLevel.GREEN) {
                lastLevelByVital.put(vitalType, AlarmLevel.GREEN);
                continue;
            }

            // same non-green level as last time: skip
            if (prev != null && prev == level) {
                continue;
            }

            // Keep full timestamp (seconds) for abnormal events sheet
            abnormalEvents.add(new AbnormalEvent(
                    v.getDateTime(),
                    vitalType,
                    v.getValue(),
                    level
            ));

            lastLevelByVital.put(vitalType, level);
        }
    }

    /* -------------------- Excel export -------------------- */

    public void exportExcel(String filename) throws Exception {

        Workbook workbook = new XSSFWorkbook();

        Sheet averagesSheet = workbook.createSheet("Vital Signs Average per Minute");
        Sheet abnormalSheet = workbook.createSheet("Abnormal Events");

        writeAveragesSheet(averagesSheet);
        writeAbnormalSheet(abnormalSheet);

        try (FileOutputStream fos = new FileOutputStream(filename)) {
            workbook.write(fos);
        }

        workbook.close();
    }

    private void writeAveragesSheet(Sheet sheet) {

        Row h = sheet.createRow(0);
        h.createCell(0).setCellValue("Date and Time");
        h.createCell(1).setCellValue("Avg Heart Rate");
        h.createCell(2).setCellValue("Avg Respiratory Rate");
        h.createCell(3).setCellValue("Avg Temperature");
        h.createCell(4).setCellValue("Avg Blood Pressure");

        int row = 1;
        for (MinuteAverage m : minuteAverages) {
            Row r = sheet.createRow(row++);
            r.createCell(0).setCellValue(m.getMinuteText());
            r.createCell(1).setCellValue(m.getAvgHeartRate());
            r.createCell(2).setCellValue(m.getAvgRespRate());
            r.createCell(3).setCellValue(m.getAvgTemperature());
            r.createCell(4).setCellValue(m.getAvgBloodPressure());
        }

        for (int i = 0; i <= 4; i++) sheet.autoSizeColumn(i);
    }

    private void writeAbnormalSheet(Sheet sheet) {

        Row h = sheet.createRow(0);
        h.createCell(0).setCellValue("Date and Time");
        h.createCell(1).setCellValue("Vital Type");
        h.createCell(2).setCellValue("Value");
        h.createCell(3).setCellValue("Level");

        int row = 1;
        for (AbnormalEvent e : abnormalEvents) {
            Row r = sheet.createRow(row++);
            r.createCell(0).setCellValue(e.getSecondText());
            r.createCell(1).setCellValue(e.getVitalType());
            r.createCell(2).setCellValue(e.getValue());
            r.createCell(3).setCellValue(e.getLevel().name());
        }

        for (int i = 0; i <= 3; i++) sheet.autoSizeColumn(i);
    }

    /* -------------------- Helper accumulator -------------------- */

    private static class MinuteAccumulator {
        private double hrSum = 0;   private int hrCount = 0;
        private double rrSum = 0;   private int rrCount = 0;
        private double tSum  = 0;   private int tCount  = 0;
        private double sysSum = 0;  private int bpCount = 0;
        private double diaSum = 0;

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
