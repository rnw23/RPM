package Alarm;

import AllVitalSigns.VitalSign;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AlarmManager {

    // 记录每个 vital 上一次的 level（用于“同 level 不重复更新弹窗”）
    private final Map<String, AlarmLevel> lastLevel = new HashMap<>();

    // ✅ 每个 vital 只保留一个弹窗
    private final Map<String, JDialog> vitalDialogs = new HashMap<>();
    private final Map<String, JOptionPane> vitalOptionPanes = new HashMap<>();

    public void applyUIAndNotify(VitalSign v, JComponent panel) {
        AlarmLevel level = v.getAlarmLevel();
        String key = v.getClass().getSimpleName();

        // 1) 背景色始终更新
        setPanelColour(panel, level);

        // 2) GREEN：关闭该 vital 的弹窗（如果存在），并更新状态
        if (level == AlarmLevel.GREEN) {
            lastLevel.put(key, level);
            closeVitalDialog(key);
            return;
        }

        // 3) 如果 level 没变化：不重复更新（避免频繁刷新/重复弹）
        AlarmLevel previous = lastLevel.get(key);
        if (previous != null && previous == level) {
            return;
        }
        lastLevel.put(key, level);

        // 4) 生成 alarm 信息
        Alarm alarm = new Alarm(v);
        alarm.sendNotification(); // 你现有的 console 输出

        // 5) 弹窗：非模态 + 每 vital 一个弹窗（AMBER 也可以显示，但不必 beep）
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

        // 已存在：更新内容即可
        if (vitalDialogs.containsKey(key) && vitalOptionPanes.containsKey(key)) {
            JOptionPane op = vitalOptionPanes.get(key);
            op.setMessage(message);

            // 更新标题（可选）
            JDialog dialog = vitalDialogs.get(key);
            dialog.setTitle(title);

            // 让它浮到前面（可选）
            dialog.toFront();
            dialog.repaint();
            return;
        }

        // 不存在：新建一个非模态 JDialog
        int msgType = (level == AlarmLevel.RED) ? JOptionPane.ERROR_MESSAGE : JOptionPane.WARNING_MESSAGE;

        JOptionPane optionPane = new JOptionPane(message, msgType);
        JDialog dialog = optionPane.createDialog(parent, title);

        dialog.setModal(false); // ✅ 关键：非模态，多个弹窗可以随机关
        dialog.setAlwaysOnTop(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // 关闭时把 Map 清掉（避免引用残留）
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