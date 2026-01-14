package UI;

import javax.swing.*;
import java.awt.*;
import RPM.PatientBase;
import Alarm.Alarm;

public class App extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    // Main application frame (controller for UI navigation) for RPM
    public App(PatientBase base) {
        super("Remote Patient Monitor");

        LoginPanel login = new LoginPanel(
                () -> showDashboard()    // delegate to App
        );

        Alarm.setUiAlarmPopupsEnabled(false);

        UI dashboard = new UI(base);

        cards.add(login, "LOGIN");
        cards.add(dashboard.getMainPanel(), "DASHBOARD");

        setContentPane(cards);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        cardLayout.show(cards, "LOGIN");
    }

    // from LoginPanel to mainDashboard
    public void showDashboard() {
        Alarm.setUiAlarmPopupsEnabled(true);    // DASHBOARD → alarms enabled
        cardLayout.show(cards, "DASHBOARD");
    }

}
