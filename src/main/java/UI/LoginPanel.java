package UI;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class LoginPanel extends JPanel {
    private JLabel logo;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private Runnable onLoginSuccess;

    public LoginPanel(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
        initialiseLoginUI();
        wireActions();
    }

    public void initialiseLoginUI() {
        setBackground(Color.white);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        //gbc.fill = GridBagConstraints.HORIZONTAL;

        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        // Logo
        logo = new JLabel(new ImageIcon("PB logo.jpeg"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0.3;
        add(logo, gbc);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.white);
        formPanel.setPreferredSize(new Dimension(320, 180)); // <-- controls how wide it looks

        GridBagConstraints f = new GridBagConstraints();
        f.insets = new Insets(6, 6, 6, 6);

        // Row 0: username
        f.gridx = 0;
        f.gridy = 0;
        f.anchor = GridBagConstraints.LINE_END;
        f.fill = GridBagConstraints.NONE;
        f.weightx = 0;
        formPanel.add(usernameLabel, f);

        f.gridx = 1;
        f.anchor = GridBagConstraints.LINE_START;
        f.fill = GridBagConstraints.NONE;          // <-- don't stretch
        f.weightx = 0;
        usernameField.setColumns(16);              // <-- controls field length
        formPanel.add(usernameField, f);

        // Row 1: password
        f.gridx = 0;
        f.gridy = 1;
        f.anchor = GridBagConstraints.LINE_END;
        formPanel.add(passwordLabel, f);

        f.gridx = 1;
        f.anchor = GridBagConstraints.LINE_START;
        passwordField.setColumns(16);              // <-- controls field length
        formPanel.add(passwordField, f);

        // Row 2: buttons (centered)
        f.gridx = 0;
        f.gridy = 2;
        f.gridwidth = 2;
        f.anchor = GridBagConstraints.CENTER;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setBackground(Color.white);

        registerButton.setPreferredSize(new Dimension(120, 32));
        registerButton.setBackground(new Color(128, 128, 235)); // steel blue
        registerButton.setForeground(Color.WHITE);

        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setOpaque(true);

        loginButton.setPreferredSize(new Dimension(120, 32));
        loginButton.setBackground(new Color(128, 128, 128));
        loginButton.setForeground(Color.WHITE);
        loginButton.setOpaque(true);

        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);

        btnPanel.add(registerButton);
        btnPanel.add(loginButton);

        formPanel.add(btnPanel, f);

        // Row 1: add formPanel under logo, centered
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 0.8;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.NONE;
        add(formPanel, gbc);
    }

    private void wireActions() {
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            char[] password = passwordField.getPassword();

            boolean ok = AuthService.check(username, password);

            // good practice: clear password char[]
            Arrays.fill(password, '\0');

            if (ok) {
                onLoginSuccess.run();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Incorrect username or password.",
                        "Login failed",
                        JOptionPane.ERROR_MESSAGE
                );
                passwordField.setText("");
                passwordField.requestFocusInWindow();
            }
        });

        // Allow Enter key to login
        passwordField.addActionListener(e -> loginButton.doClick());
    }
}