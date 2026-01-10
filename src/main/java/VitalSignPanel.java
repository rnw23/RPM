import javax.swing.*;
import java.awt.*;
import java.util.List;
import AllVitalSigns.VitalSign;

public class VitalSignPanel extends JPanel {

    private List<? extends VitalSign> data;
    private String title;
    private int maxPoints = 30;

    private final int PAD = 40;

    public VitalSignPanel(String title) {
        this.title = title;
        setPreferredSize(new Dimension(350, 250));
    }

    public void updateData(List<? extends VitalSign> data) {
        this.data = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (data == null || data.size() < 2) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // ---- Title ----
        g2.drawString(title, 10, 20);

        int size = Math.min(data.size(), maxPoints);
        int start = data.size() - size;

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (int i = start; i < data.size(); i++) {
            double v = data.get(i).getValue();
            min = Math.min(min, v);
            max = Math.max(max, v);
        }

        if (max - min < 0.01) {
            max += 1;
            min -= 1;
        }

        // ---- Draw Axes ----
        int x0 = PAD;
        int y0 = h - PAD;
        int x1 = w - PAD;
        int y1 = PAD;

        g2.drawLine(x0, y0, x1, y0);   // X axis
        g2.drawLine(x0, y0, x0, y1);   // Y axis

        // ---- Y Axis Ticks ----
        int ticks = 5;
        for (int i = 0; i <= ticks; i++) {
            int y = y0 - i * (y0 - y1) / ticks;
            double value = min + i * (max - min) / ticks;

            g2.drawLine(x0 - 5, y, x0 + 5, y);
            g2.drawString(String.format("%.1f", value), 5, y + 5);
        }

        // ---- X Axis Ticks (seconds) ----
        for (int i = 0; i <= ticks; i++) {
            int x = x0 + i * (x1 - x0) / ticks;
            int secondsAgo = (ticks - i) * (maxPoints / ticks);

            g2.drawLine(x, y0 - 5, x, y0 + 5);
            g2.drawString(secondsAgo + "s", x - 10, y0 + 20);
        }

        // ---- Plot Line (THICK) ----
        g2.setStroke(new BasicStroke(3f));   // thickness

        int prevX = x0;
        int prevY = scale(data.get(start).getValue(), min, max, h);

        for (int i = 1; i < size; i++) {
            int x = x0 + i * (x1 - x0) / size;
            int y = scale(data.get(start + i).getValue(), min, max, h);

            g2.drawLine(prevX, prevY, x, y);

            prevX = x;
            prevY = y;
        }
    }

    private int scale(double value, double min, double max, int height) {
        double normalized = (value - min) / (max - min);
        return (int) ((height - 2 * PAD) * (1 - normalized)) + PAD;
    }
}
