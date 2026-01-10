package Alarm;

import AllVitalSigns.VitalSign;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AlarmManager {

    // record every signal from last signal
    private final Map<String, AlarmLevel> lastLevel = new HashMap<>();

    // one notify every signal
    private final Map<String, JDialog> vitalDialogs = new HashMap<>();
    private final Map<String, JOptionPane> vitalOptionPanes = new HashMap<>();

    public void applyUIAndNotify(VitalSign v, JComponent panel) {
        AlarmLevel level = v.getAlarmLevel();
        String key = v.getClass().getSimpleName();

        // 1)
        setPanelColour(panel, level);

        // 2) GREEN
        if (level == AlarmLevel.GREEN) {
            lastLevel.put(key, level);
            closeVitalDialog(key);
            return;
        }

        // 3) level changes but not updated
        AlarmLevel previous = lastLevel.get(key);
        if (previous != null && previous == level) {
            return;
        }
        lastLevel.put(key, level);

        // 4)alarm info
        Alarm alarm = new Alarm(v);
        alarm.sendNotification();

        // 5) one vital one notify
        SwingUtilities.invokeLater(() -> {
            if (level == AlarmLevel.RED) {
                Toolkit.getDefaultToolkit().beep(); // 只对 RED 响，避免 AMBER 烦
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