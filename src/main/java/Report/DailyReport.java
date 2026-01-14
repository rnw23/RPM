package Report;

import org.apache.poi.ss.usermodel.Row;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/**
 * DailyReport generates a Excel report for a patient that last for a day.
 * The report is immediately written and save the report into a dedicated directory.
 */
public class DailyReport extends Report {

    private final Path filePath;

    public DailyReport(String patientName,
                       LocalDate date,
                       List<MinuteAverage> minuteAverages,
                       List<AbnormalEvent> abnormalEvents) {
        super();
        ensureReportsDir();

        this.filePath = Path.of(
                "reports",
                "DailyReport_" + date + "_" + safe(patientName) + ".xlsx"
        );

        writeMinuteAverages(minuteAverages);
        writeAbnormalEvents(abnormalEvents);

        // Persist the workbook as an Excel file
        saveToDisk();
    }

    // Ensures the reports directory exists before writing output files.
    private static void ensureReportsDir() {
        try {
            Files.createDirectories(Path.of("reports"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create reports directory", e);
        }
    }

    // Sanitises patient name to prevent invalid filenames.
    private static String safe(String name) {
        return name.replaceAll("[^a-zA-Z0-9\\-_ ]", "_").trim();
    }

    // Writes all minute-average rows into Sheet 1.
    private void writeMinuteAverages(List<MinuteAverage> list) {
        int r = 1;
        for (MinuteAverage m : list) {
            Row row = avgSheet.createRow(r++);
            row.createCell(0).setCellValue(m.getMinuteText());
            row.createCell(1).setCellValue(m.getAvgHeartRate());
            row.createCell(2).setCellValue(m.getAvgRespRate());
            row.createCell(3).setCellValue(m.getAvgTemperature());
            row.createCell(4).setCellValue(m.getAvgBloodPressure());
        }
    }

    // Writes all abnormal-episode rows into Sheet 2.
    private void writeAbnormalEvents(List<AbnormalEvent> list) {
        int r = 1;
        for (AbnormalEvent e : list) {
            Row row = abnormalSheet.createRow(r++);
            row.createCell(0).setCellValue(e.getStartText());
            row.createCell(1).setCellValue(e.getEndText());
            row.createCell(2).setCellValue(e.getVitalType());
            row.createCell(3).setCellValue(e.getValueRangeText());
            row.createCell(4).setCellValue(e.getLevel().name());
        }
    }

    /**
     * Saves the workbook to disk as an XLSX file.
     *
     * Reference 3 - The approach for writing the Apache POI workbook to an .xlsx file (FileOutputStream + workbook.write)
     * was produced with assistance from ChatGPT (AI) as part of the prototyping process.
     * The data model (MinuteAverage/AbnormalEvent) were designed by the team.
     * AI support was used only for the mechanics/pattern of exporting to Excel.
     */
    private void saveToDisk() {
        try (FileOutputStream out = new FileOutputStream(filePath.toFile())) {
            workbook.write(out);
            workbook.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Path getFilePath() {
        return filePath;
    }
}
