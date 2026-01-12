package Report;

import RPM.Patient;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

public class DailyReport {

    private final List<MinuteAverage> minuteAverages;
    private final List<AbnormalEvent> abnormalEvents;

    public DailyReport(PermanentRecord record, Patient patient, LocalDate date) {
        this.minuteAverages = record.getMinuteAverages(patient.getId(), date);
        this.abnormalEvents = record.getAbnormalEvents(patient.getId(), date);
    }

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
}
