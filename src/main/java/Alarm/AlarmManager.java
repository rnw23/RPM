package Alarm;

import java.time.Duration;
import java.time.LocalDateTime;

import AllVitalSigns.VitalSign;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AlarmManager {

    private final Map<String, AlarmLevel> lastLevel = new HashMap<>();
    private final Map<String, JDialog> vitalDialogs = new HashMap<>();
    private final Map<String, JOptionPane> vitalOptionPanes = new HashMap<>();

    private String recipientEmail = "your@email.com";

    private final Duration emailCooldown = Duration.ofSeconds(10);
    private final Map<String, LocalDateTime> lastEmailSent = new HashMap<>();

    private AlarmEmailService emailService = null;


    public void configureEmail(String smtpHost, int smtpPort, String senderEmail, String appPassword, boolean useTls) {
        this.emailService = new AlarmEmailService(smtpHost, smtpPort, senderEmail, appPassword, useTls);
    }

    public void setRecipientEmail(String email) {
        if (email == null) return;
        email = email.trim();
        if (!email.isEmpty()) this.recipientEmail = email;
    }

    public void applyUIAndNotify(VitalSign v, JComponent panel) {
        AlarmLevel level = v.getAlarmLevel();
        String key = v.getClass().getSimpleName();

        setPanelColour(panel, level);

        if (level == AlarmLevel.GREEN) {
            lastLevel.put(key, level);
            closeVitalDialog(key);
            return;
        }

        AlarmLevel previous = lastLevel.get(key);
        if (previous != null && previous == level) {
            return;
        }
        lastLevel.put(key, level);

        Alarm alarm = new Alarm(v);
        alarm.sendNotification();

        if (level == AlarmLevel.RED && emailService != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime last = lastEmailSent.get(key);

            boolean inCooldown = (last != null) && Duration.between(last, now).compareTo(emailCooldown) < 0;

            if (!inCooldown) {
                String subject = "RED ALARM: " + key + " (" + level + ")";
                String body = alarm.getMessage();

                new Thread(() -> emailService.sendEmail(recipientEmail, subject, body), "AlarmEmail-" + key).start();
                lastEmailSent.put(key, now);
            }
        }

        SwingUtilities.invokeLater(() -> {
            if (level == AlarmLevel.RED) {
                Toolkit.getDefaultToolkit().beep();
            }
            showOrUpdateVitalDialog(
                    key,
                    panel,
                    "ALARM - " + key + " (" + level + ")",
                    alarm.getMessage(),
                    level
            );
        });
    }

    private void showOrUpdateVitalDialog(String key, Component parent, String title, String message, AlarmLevel level) {


        if (vitalDialogs.containsKey(key) && vitalOptionPanes.containsKey(key)) {
            JOptionPane op = vitalOptionPanes.get(key);
            op.setMessage(message);

            // update title
            JDialog dialog = vitalDialogs.get(key);
            dialog.setTitle(title);

            //front）
            dialog.toFront();
            dialog.repaint();
            return;
        }

        //JDialog
        int msgType = (level == AlarmLevel.RED) ? JOptionPane.ERROR_MESSAGE : JOptionPane.WARNING_MESSAGE;

        JOptionPane optionPane = new JOptionPane(message, msgType);
        JDialog dialog = optionPane.createDialog(parent, title);

        dialog.setModal(false); //random clods of display
        dialog.setAlwaysOnTop(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        //
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                vitalDialogs.remove(key);
                vitalOptionPanes.remove(key);
            }
        });

        vitalDialogs.put(key, dialog);
        vitalOptionPanes.put(key, optionPane);

        dialog.setVisible(true);
    }

    private void closeVitalDialog(String key) {
        JDialog dialog = vitalDialogs.get(key);
        if (dialog != null) {
            dialog.dispose();
        }
        vitalDialogs.remove(key);
        vitalOptionPanes.remove(key);
    }

    //for ui not sure but work
    public void closeDialogForVital(String vitalKey) {
        closeVitalDialog(vitalKey);
    }
    // === close all alarm diaglog ===
    public void closeAllDialogs() {
        //avoid ConcurrentModification
        for (JDialog d : vitalDialogs.values()) {
            if (d != null) d.dispose();
        }
        vitalDialogs.clear();
        vitalOptionPanes.clear();
    }

    private void setPanelColour(JComponent panel, AlarmLevel level) {
        Color color = switch (level) {
            case GREEN -> new Color(210, 255, 210);
            case AMBER -> new Color(255, 235, 170);
            case RED -> new Color(255, 180, 180);
        };

        panel.setOpaque(true);
        panel.setBackground(color);
        panel.repaint();
    }
}