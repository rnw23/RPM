import Report.DailyReport;
import RPM.*;
import UI.UI;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) {

        Patient p = new Patient(1, "John Smith", 35, "Ward B", "123456789");

        // ---------------------------
        // 1. Launch UI (EDT thread)
        // ---------------------------
        SwingUtilities.invokeLater(() -> {
            new UI().initialise();
        });

        // ---------------------------
        // 2. Run testing in background thread
        // ---------------------------
        new Thread(() -> {
            try {
                PatientBase database = new PatientBase();
                Patient p1 = database.getPatient(0);   // test first patient

                // Simulate 60 seconds of data
                for (int i = 0; i < 60; i++) {
                    p.updateVitals();
                    System.out.println(p.PatientDisplay());
                    Thread.sleep(1000);
                }

                // ---------------------------
                // 3. Generate Daily Report
                // ---------------------------
                DailyReport report = new DailyReport(p);

                String date = LocalDate.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                String safeName = p.getName()
                        .trim()
                        .replaceAll("\\s+", "_")
                        .replaceAll("[^a-zA-Z0-9_\\-]", "");

                String filename = "DailyReport_" + date + "_" + safeName + ".xlsx";

                report.exportExcel(filename);

                System.out.println("Daily report generated successfully: " + filename);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}

/* ----- testing Listener -----
    public static void main(String[] args) {

        RPM.VitalSignsGenerator simulator = new RPM.VitalSignsGenerator();

        AlarmLevel alarm = new AlarmLevel();
        UI.UI ui = new UI.UI();
        DatabaseRepository repo = new DatabaseRepository();

        simulator.addListener(alarm);
        simulator.addListener(ui);
        simulator.addListener(repo);

        simulator.start();
    }
 */