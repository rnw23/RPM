package DailyReport;

import Alarm.AlarmLevel;
import AllVitalSigns.*;
import RPM.Patient;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class DailyReport {

    private final List<MinuteAverage> minuteAverages = new ArrayList<>();
    private final List<AbnormalEvent> abnormalEvents = new ArrayList<>();

    public DailyReport(Patient patient) {
        generateMinuteAverages(patient);
        detectAbnormalEvents(patient);
    }

    /* ---------------- TIME-BASED MINUTE AVERAGES ---------------- */

    private void generateMinuteAverages(Patient patient) {

        Map<LocalDateTime, List<VitalSign>> bucket = new HashMap<>();

        addToBucket(bucket, patient.getHeartRateHistory());
        addToBucket(bucket, patient.getRespRateHistory());
        addToBucket(bucket, patient.getTemperatureHistory());

        for (LocalDateTime minute : bucket.keySet().stream().sorted().toList()) {
            List<VitalSign> signs = bucket.get(minute);

            double hrAvg = avg(signs, HeartRate.class);
            double rrAvg = avg(signs, RespRate.class);
            double tempAvg = avg(signs, Temperature.class);

            minuteAverages.add(new MinuteAverage(minute, hrAvg, rrAvg, tempAvg));
        }
    }

    private <T extends VitalSign> void addToBucket(
            Map<LocalDateTime, List<VitalSign>> bucket,
            List<T> history) {

        for (VitalSign v : history) {
            LocalDateTime minuteKey =
                    v.getDateTime().truncatedTo(ChronoUnit.MINUTES);

            bucket.computeIfAbsent(minuteKey, k -> new ArrayList<>()).add(v);
        }
    }

    private double avg(List<VitalSign> list, Class<?> clazz) {
        List<VitalSign> filtered =
                list.stream().filter(clazz::isInstance).toList();

        if (filtered.isEmpty()) return 0;

        return filtered.stream()
                .mapToDouble(VitalSign::getValue)
                .average()
                .orElse(0);
    }

    /* ---------------- ABNORMAL EVENTS ---------------- */

    private void detectAbnormalEvents(Patient patient) {
        collectAbnormal(patient.getHeartRateHistory());
        collectAbnormal(patient.getRespRateHistory());
        collectAbnormal(patient.getTemperatureHistory());
        collectAbnormal(patient.getBloodPressureHistory());
    }

    private <T extends VitalSign> void collectAbnormal(List<T> history) {
        for (VitalSign v : history) {
            if (v.getAlarmLevel() != AlarmLevel.GREEN) {
                abnormalEvents.add(new AbnormalEvent(v));
            }
        }
    }

    /* ---------------- EXCEL EXPORT ---------------- */

    public void exportExcel(String filename) throws Exception {

        Workbook wb = new XSSFWorkbook();

        Sheet avgSheet = wb.createSheet("Minute Averages");
        Sheet alarmSheet = wb.createSheet("Abnormal Events");

        writeAverages(avgSheet);
        writeAlarms(alarmSheet);

        try (FileOutputStream fos = new FileOutputStream(filename)) {
            wb.write(fos);
        }

        wb.close();
    }

    private void writeAverages(Sheet sheet) {
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Minute");
        header.createCell(1).setCellValue("Avg Heart Rate");
        header.createCell(2).setCellValue("Avg Resp Rate");
        header.createCell(3).setCellValue("Avg Temperature");

        int rowIdx = 1;
        for (MinuteAverage m : minuteAverages) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(m.getMinute().toString());
            row.createCell(1).setCellValue(m.getAvgHeartRate());
            row.createCell(2).setCellValue(m.getAvgRespRate());
            row.createCell(3).setCellValue(m.getAvgTemperature());
        }
    }

    private void writeAlarms(Sheet sheet) {
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Time");
        header.createCell(1).setCellValue("Vital");
        header.createCell(2).setCellValue("Value");
        header.createCell(3).setCellValue("Level");

        int rowIdx = 1;
        for (AbnormalEvent e : abnormalEvents) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(e.getDateTime().toString());
            row.createCell(1).setCellValue(e.getVitalName());
            row.createCell(2).setCellValue(e.getValue());
            row.createCell(3).setCellValue(e.getLevel().name());
        }
    }
}