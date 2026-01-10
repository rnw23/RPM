/* ----- testing Daily Report ----- */
public class Main {

    public static void main(String[] args) throws Exception {

        Patient p = new Patient(1, "John Smith", 35);

        for (int i = 0; i < 60; i++) { // 24 minutes
            p.updateVitals();
            Thread.sleep(1000);
        }

        DailyReport report = new DailyReport(p);
        report.exportExcel("DailyReport_Patient_1.xlsx");

        System.out.println("Daily report generated successfully.");
    }
}

/* ----- testing UI + Patient -----
public class Main {

    public static void main(String[] args) throws InterruptedException {
        Patient p = new Patient(1,"John Smith", 35);

        SwingUtilities.invokeLater(() -> {
            new UI().initialise();
        });

        while (true) {
            p.updateVitals();
            System.out.println(p.PatientDisplay());
            Thread.sleep(1000);
        }
    }
}
 */

/* ----- testing Listener -----
    public static void main(String[] args) {

        VitalSignsGenerator simulator = new VitalSignsGenerator();

        AlarmLevel alarm = new AlarmLevel();
        UI ui = new UI();
        DatabaseRepository repo = new DatabaseRepository();

        simulator.addListener(alarm);
        simulator.addListener(ui);
        simulator.addListener(repo);

        simulator.start();
    }
 */