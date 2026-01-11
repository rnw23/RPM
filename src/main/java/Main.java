import DailyReport.DailyReport;
import RPM.Patient;
import UI.UI;

import javax.swing.*;

/* ----- testing Daily Report + UI.UI + RPM.Patient ----- */
public class Main {

    public static void main(String[] args) throws Exception {

        Patient p = new Patient(1, "John Smith", 35);

        SwingUtilities.invokeLater(() -> {
            new UI().initialise();
        });

        for (int i = 0; i < 60; i++) { // 24 minutes
            p.updateVitals();
            System.out.println(p.PatientDisplay());
            Thread.sleep(1000);
        }

        DailyReport report = new DailyReport(p);
        report.exportExcel("DailyReport_Patient_1.xlsx");

        System.out.println("Daily report generated successfully.");
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