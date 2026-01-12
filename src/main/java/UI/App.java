package UI;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    public App() {
        super("Remote Patient Monitor");
        LoginPanel login = new LoginPanel(
                () -> cardLayout.show(cards, "DASHBOARD")
        );

        UI dashboard = new UI();

        cards.add(login, "LOGIN");
        cards.add(dashboard.getMainPanel(), "DASHBOARD");

        setContentPane(cards);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 900);
        setLocationRelativeTo(null);

        cardLayout.show(cards, "LOGIN");
        setVisible(true);
    }
}
