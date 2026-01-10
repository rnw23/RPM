import Alarm.AlarmLevel;

import javax.swing.*;
public class Main {

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
}