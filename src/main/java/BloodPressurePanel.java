import javax.swing.*;
import java.awt.*;
import java.util.List;
import AllVitalSigns.BloodPressure;

public class BloodPressurePanel extends JPanel {

    private List<BloodPressure> data;
    private int maxPoints = 30;
    private final int PAD = 40;

    public BloodPressurePanel() {
        setPreferredSize(new Dimension(350, 250));
    }

    public void updateData(List<BloodPressure> data) {
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

//        g2.drawString("Blood Pressure (Systolic/Diastolic)", 10, 20);

        int size = Math.min(data.size(), 30);
        int start = data.size() - size;

        int min = 50;
        int max = 200;

        int x0 = PAD;
        int y0 = h - PAD;
        int x1 = w - PAD;
        int y1 = PAD;

        // ---- Draw Axes ----
        g2.drawLine(x0, y0, x1, y0);   // X axis
        g2.drawLine(x0, y0, x0, y1);   // Y axis

        // ---- Y Axis Ticks (intervals) ----
        int yTicks = 6; // e.g., every 30 mmHg from 50 to 200
        for (int i = 0; i <= yTicks; i++) {
            int y = y0 - i * (y0 - y1) / yTicks;
            double value = min + i * (max - min) / yTicks;

            g2.drawLine(x0 - 5, y, x0 + 5, y);
            g2.drawString(String.format("%.0f", value), 5, y + 5);
        }

        // ---- X Axis Ticks (time in seconds) ----
        int xTicks = 6; // for 0,5,10,...,30
        for (int i = 0; i <= xTicks; i++) {
            int x = x0 + i * (x1 - x0) / xTicks;
            int seconds = i * (maxPoints / xTicks); // forward time

            g2.drawLine(x, y0 - 5, x, y0 + 5);
            g2.drawString(seconds + "s", x - 10, y0 + 20);
        }

        // ---- Plot Lines ----
        g2.setStroke(new BasicStroke(3f));

        // Systolic (RED)
        g2.setColor(Color.RED);
        drawLineSeries(g2, data, start, size, x0, x1, min, max, h, true);

        // Diastolic (BLUE)
        g2.setColor(Color.BLUE);
        drawLineSeries(g2, data, start, size, x0, x1, min, max, h, false);

        // Legend
        g2.setColor(Color.RED);
        g2.drawString("Systolic", w - 120, 20);
        g2.setColor(Color.BLUE);
        g2.drawString("Diastolic", w - 120, 35);
    }


    private void drawLineSeries(Graphics2D g2, List<BloodPressure> data,
                                int start, int size,
                                int x0, int x1,
                                int min, int max,
                                int h,
                                boolean systolic) {

        int prevX = x0;
        double prevValue = systolic ? data.get(start).getSystole() : data.get(start).getDiastole();

        int prevY = scale(prevValue, min, max, h);

        for (int i = 1; i < size; i++) {
            int x = x0 + i * (x1 - x0) / size;

            double value = systolic
                    ? data.get(start + i).getSystole()
                    : data.get(start + i).getDiastole();

            int y = scale(value, min, max, h);

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
