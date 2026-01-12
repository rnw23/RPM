import Report.DailyReport;
import Report.PermanentRecord;
import RPM.Patient;
import RPM.PatientBase;
import UI.UI;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) {

        PatientBase base = new PatientBase();
        base.addPatient(new Patient(1, "John Smith", 28, "Ward A", "01234567890", 0));
        base.addPatient(new Patient(2, "Alice Brown", 35, "Ward B", "01234567891", 0));
        base.addPatient(new Patient(3, "David Jones", 42, "Ward C", "01234567892", 1));
        base.addPatient(new Patient(4, "Jennifer Baker", 49, "Ward D", "01234567893", 1));

        PermanentRecord record = new PermanentRecord();

        SwingUtilities.invokeLater(() -> new UI(base, record).initialise());

        // report thread (example: after 60 seconds)
        new Thread(() -> {
            try {
                Thread.sleep(60_000);

                Patient reportPatient = base.getPatient(0);
                if (reportPatient == null) return;

                LocalDate today = LocalDate.now();

                DailyReport report = new DailyReport(record, reportPatient, today);

                String date = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                String safeName = reportPatient.getName()
                        .trim()
                        .replaceAll("\\s+", "_")
                        .replaceAll("[^a-zA-Z0-9_\\-]", "");

                String filename = "DailyReport_" + date + "_" + safeName + ".xlsx";
                report.exportExcel(filename);

                System.out.println("Daily report generated successfully: " + filename);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "ReportThread").start();
    }
}
