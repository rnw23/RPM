package Report;

import org.apache.poi.ss.usermodel.Row;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

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
        saveToDisk();
    }

    private static void ensureReportsDir() {
        try {
            Files.createDirectories(Path.of("reports"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create reports directory", e);
        }
    }

    private static String safe(String name) {
        return name.replaceAll("[^a-zA-Z0-9\\-_ ]", "_").trim();
    }

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
