package Report;

import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class PermanentRecord extends Report {

    private final Path filePath;

    public PermanentRecord(String patientName) {
        super();
        ensureRecordsDir();
        this.filePath = Path.of("records", "permanentRecord_" + safe(patientName) + ".xlsx");
        // create the file on disk immediately
        saveToDisk();
    }

    private static void ensureRecordsDir() {
        try {
            Files.createDirectories(Path.of("records"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create records directory", e);
        }
    }

    private static String safe(String name) {
        return name.replaceAll("[^a-zA-Z0-9\\-_ ]", "_").trim();
    }

    public Path getFilePath() {
        return filePath;
    }

    /** Append a single minute average row and persist to disk */
    public synchronized void appendMinuteAverage(MinuteAverage m) {
        int r = nextRowIndex(avgSheet);
        Row row = avgSheet.createRow(r);
        row.createCell(0).setCellValue(m.getMinuteText());
        row.createCell(1).setCellValue(m.getAvgHeartRate());
        row.createCell(2).setCellValue(m.getAvgRespRate());
        row.createCell(3).setCellValue(m.getAvgTemperature());
        row.createCell(4).setCellValue(m.getAvgBloodPressure());

        saveToDisk();
    }

    /** Append a single abnormal event row and persist to disk */
    public synchronized void appendAbnormalEvent(AbnormalEvent e) {
        int r = nextRowIndex(abnormalSheet);
        Row row = abnormalSheet.createRow(r);
        row.createCell(0).setCellValue(e.getStartText());
        row.createCell(1).setCellValue(e.getEndText());
        row.createCell(2).setCellValue(e.getVitalType());
        row.createCell(3).setCellValue(e.getValueRangeText());
        row.createCell(4).setCellValue(e.getLevel().name());

        saveToDisk();
    }

    /** Writes the current workbook state to the permanent record file */
    private synchronized void saveToDisk() {
        try (FileOutputStream out = new FileOutputStream(filePath.toFile())) {
            workbook.write(out);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Export/copy the already-existing permanent record file to a user-chosen location.
     * (The "download" requirement.)
     */
    public void copyTo(Path destination) throws IOException {
        Files.copy(filePath, destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }
}