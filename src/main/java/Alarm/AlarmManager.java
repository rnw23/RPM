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
    //patient to email
    private String currentPatientName = "Unknown";

    public void setCurrentPatientName(String name) {
        if (name == null) return;
        name = name.trim();
        if (!name.isEmpty()) this.currentPatientName = name;
    }

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

        /*
        AlarmLevel previous = lastLevel.get(key);
        if (previous != null && previous == level) {
            return;
        }

         */

        AlarmLevel previous = lastLevel.get(key);
        if (previous != null && previous == level) {

            if (Alarm.isUiAlarmPopupsEnabled()) {
                JDialog d = vitalDialogs.get(key);   // your map of dialogs
                if (d == null || !d.isVisible()) {
                    // fall through (do NOT return)
                } else {
                    return; // already showing
                }
            } else {
                return; // popups disabled, ignore repeats
            }
        }

        lastLevel.put(key, level);

        Alarm alarm = new Alarm(v);

        if (level == AlarmLevel.RED && emailService != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime last = lastEmailSent.get(key);

            boolean inCooldown = (last != null) && Duration.between(last, now).compareTo(emailCooldown) < 0;

            if (!inCooldown) {
                String subject = "RED ALARM: " + currentPatientName + " - " + key + " (" + level + ")";
                String body = "Patient: " + currentPatientName + "\n" + alarm.getMessage();

                new Thread(() -> emailService.sendEmail(recipientEmail, subject, body), "AlarmEmail-" + key).start();
                lastEmailSent.put(key, now);
            }
        }

        if (!Alarm.isUiAlarmPopupsEnabled()) {
            return;
        }

        //setPanelColour(panel, level);

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


/*
    public void applyUIAndNotify(VitalSign v, JComponent panel) {
        AlarmLevel level = v.getAlarmLevel();
        String key = v.getClass().getSimpleName();

        // ✅ Always reflect GREEN on the panel (so colours reset properly)
        if (level == AlarmLevel.GREEN) {
            setPanelColour(panel, level);     // <-- put this back for GREEN
            lastLevel.put(key, level);
            closeVitalDialog(key);
            return;
        }

        // De-dupe repeated same-level alarms
        AlarmLevel previous = lastLevel.get(key);
        if (previous != null && previous == level) {
            return;
        }
        lastLevel.put(key, level);

        Alarm alarm = new Alarm(v);

        // Email logic still allowed even on login
        if (level == AlarmLevel.RED && emailService != null) {
            // ... your cooldown code unchanged ...
        }

        // 🔒 Gate only the popup UI (login screen)
        if (!Alarm.isUiAlarmPopupsEnabled()) {
            return;
        }

        // ✅ After login, update panel colour for non-green too
        setPanelColour(panel, level);

        SwingUtilities.invokeLater(() -> {
            if (level == AlarmLevel.RED) Toolkit.getDefaultToolkit().beep();

            showOrUpdateVitalDialog(
                    key,
                    panel,
                    "ALARM - " + key + " (" + level + ")",
                    alarm.getMessage(),
                    level
            );
        });
    }

*/

    private void showOrUpdateVitalDialog(String key, Component parent, String title, String message, AlarmLevel level) {

        // Jdialog exisit but be covered
        if (vitalDialogs.containsKey(key)) {
            JDialog d = vitalDialogs.get(key);
            JLabel label = (JLabel) d.getContentPane().getComponent(0);
            label.setText(toHtmlSmall(message));

            d.pack();
            positionTopRightInPanel(d, parent);
            d.setVisible(true);
            d.toFront();
            return;
        }

        //small toast dialog
        //JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent));
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner != null ? owner : JOptionPane.getRootFrame());

        dialog.setUndecorated(true);         //no big frame
        dialog.setAlwaysOnTop(true);
        dialog.setModal(false);

        JLabel label = new JLabel(toHtmlSmall(message));
        label.setFont(label.getFont().deriveFont(11f));  //smaller texte
        label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        //bg color of dialog
        Color bg = switch (level) {
            case RED -> new Color(255, 220, 220);
            case AMBER -> new Color(255, 245, 210);
            default -> new Color(230, 255, 230);
        };
        label.setOpaque(true);
        label.setBackground(bg);
        label.setForeground(Color.DARK_GRAY);

        dialog.getContentPane().add(label);
        dialog.pack();
        positionTopRightInPanel(dialog, parent);
        dialog.setVisible(true);

        vitalDialogs.put(key, dialog);

        //optional disappear 2s
        //new javax.swing.Timer(2000, e -> closeVitalDialog(key)) {{
        //setRepeats(false);
        // start();
        // }};
    }

    private void positionTopRightInPanel(JDialog dialog, Component parent) {
        if (parent == null) return;
        try {
            Point p = parent.getLocationOnScreen();  // panel 左上角(屏幕坐标)
            int margin = 8;

            int x = p.x + parent.getWidth() - dialog.getWidth() - margin;
            int y = p.y + margin;

            dialog.setLocation(x, y);
        } catch (IllegalComponentStateException ignored) {
            Window w = SwingUtilities.getWindowAncestor(parent);
            if (w != null) dialog.setLocationRelativeTo(w);
            else dialog.setLocationRelativeTo(null);

        }
    }

    private String toHtmlSmall(String msg) {
        //change the line auto
        return "<html><div style='width:220px;'>" + msg.replace("\n", "<br>") + "</div></html>";
    }

    private void positionTopRight(JDialog dialog, Window owner, String key) {
        if (owner == null) return;

        try {
            Point p = owner.getLocationOnScreen();

            int marginRight = 16;
            int marginTop = 60;
            int gapY = 10;

            //tp avoid overlap
            int slot = Math.floorMod(key.hashCode(), 4); //max 4
            int x = p.x + owner.getWidth() - dialog.getWidth() - marginRight;
            int y = p.y + marginTop + slot * (dialog.getHeight() + gapY);

            dialog.setLocation(x, y);
        } catch (IllegalComponentStateException ignored) {

        }
    }

    private void closeVitalDialog(String key) {
        JDialog dialog = vitalDialogs.get(key);
        if (dialog != null) {
            dialog.dispose();
        }
        vitalDialogs.remove(key);
        vitalOptionPanes.remove(key);
    }

    public void closeDialogForVital(String vitalKey) {
        closeVitalDialog(vitalKey);
    }

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