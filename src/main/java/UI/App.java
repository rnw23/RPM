package UI;

import javax.swing.*;
import java.awt.*;
import RPM.PatientBase;
import Alarm.Alarm;

public class App extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    public App(PatientBase base) {
        super("Remote Patient Monitor");
        //PatientBase patientBase = new PatientBase();

        /*
        LoginPanel login = new LoginPanel(
                () -> cardLayout.show(cards, "DASHBOARD")
        );

         */
        LoginPanel login = new LoginPanel(
                () -> showDashboard()    // 🔑 delegate to App
        );

        Alarm.setUiAlarmPopupsEnabled(false);

        UI dashboard = new UI(base);
        //cards.add(dashboard, "DASHBOARD");

        cards.add(login, "LOGIN");
        cards.add(dashboard.getMainPanel(), "DASHBOARD");

        setContentPane(cards);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 900);
        setLocationRelativeTo(null);

        cardLayout.show(cards, "LOGIN");
        //Alarm.setUiAlarmPopupsEnabled(true);
        //setVisible(true);
    }

    public void showDashboard() {
        Alarm.setUiAlarmPopupsEnabled(true);    // DASHBOARD → alarms enabled
        cardLayout.show(cards, "DASHBOARD");
    }

}
