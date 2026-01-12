package Report;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class Report {

    protected static final String SHEET_AVG = "Vital Signs Average per Minute";
    protected static final String SHEET_ABN = "Abnormal Events";

    protected final Workbook workbook;
    protected final Sheet avgSheet;
    protected final Sheet abnormalSheet;

    protected Report() {
        this.workbook = new XSSFWorkbook();
        this.avgSheet = workbook.createSheet(SHEET_AVG);
        this.abnormalSheet = workbook.createSheet(SHEET_ABN);
        createHeaders();
    }

    protected void createHeaders() {
        // Sheet 1: Vital Signs Average per Minute
        Row h = avgSheet.createRow(0);
        h.createCell(0).setCellValue("Date and Time");
        h.createCell(1).setCellValue("Avg Heart Rate");
        h.createCell(2).setCellValue("Avg Respiratory Rate");
        h.createCell(3).setCellValue("Avg Temperature");
        h.createCell(4).setCellValue("Avg Blood Pressure");

        // Sheet 2: Abnormal Events
        Row h2 = abnormalSheet.createRow(0);
        h2.createCell(0).setCellValue("Start Date and Time");
        h2.createCell(1).setCellValue("End Date and Time");
        h2.createCell(2).setCellValue("Vital Type");
        h2.createCell(3).setCellValue("Value Range (Min-Max)");
        h2.createCell(4).setCellValue("Level");
    }

    protected static int nextRowIndex(Sheet sheet) {
        // returns next write position (safe even if only header exists)
        int last = sheet.getLastRowNum();
        if (last == 0 && sheet.getRow(0) == null) return 0;
        return last + 1;
    }
}
