import javax.swing.*;
public class Main {

    public static void main(String[] args) {

        VitalSignsGenerator simulator = new VitalSignsGenerator();

        AlarmEngine alarm = new AlarmEngine();
        DashboardUI ui = new DashboardUI();
        DatabaseRepository repo = new DatabaseRepository();

        simulator.addListener(alarm);
        simulator.addListener(ui);
        simulator.addListener(repo);

        simulator.start();
    }
}