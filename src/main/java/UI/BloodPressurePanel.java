package UI;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import AllVitalSigns.BloodPressure;

/**
 * JPanel for plotting blood pressure data (systolic + diastolic) in real-time
 * Systolic red, diastolic blue
 */
public class BloodPressurePanel extends JPanel {

    private List<BloodPressure> data; // BloodPressure data points to display
    private int maxPoints = 30; // maximum number of points to display
    private final int PAD = 40; // padding around the chart edges

    public BloodPressurePanel() {
        setPreferredSize(new Dimension(350, 250));
    }

    //update the bp data and repaint the panel
    public void updateData(List<BloodPressure> data) {
        this.data = data;
        repaint();
    }

    //set max no. of data points to display
    public void setMaxPoints(int maxPoints) {
        this.maxPoints = Math.max(2, maxPoints);
        repaint();
    }

    //draw chart
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (data == null || data.size() < 2) return;// if insufficient data don't draw

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        int size = Math.min(data.size(), maxPoints);
        int start = data.size() - size;

        // fixed min/max for blood pressure chart
        int min = 50;
        int max = 200;

        // define chart boundaries
        int x0 = PAD;
        int y0 = h - PAD;
        int x1 = w - PAD;
        int y1 = PAD;

        //axes
        g2.drawLine(x0, y0, x1, y0);
        g2.drawLine(x0, y0, x0, y1);

        //yaxis
        int yTicks = 6;
        for (int i = 0; i <= yTicks; i++) {
            int y = y0 - i * (y0 - y1) / yTicks;
            double value = min + i * (max - min) / yTicks;

            g2.drawLine(x0 - 5, y, x0 + 5, y);
            g2.drawString(String.format("%.0f", value), 5, y + 5);
        }

        //xaxis
        int xTicks = 6;
        for (int i = 0; i <= xTicks; i++) {
            int x = x0 + i * (x1 - x0) / xTicks;
            int seconds = i * (maxPoints / xTicks);

            g2.drawLine(x, y0 - 5, x, y0 + 5);
            g2.drawString(seconds + "s", x - 10, y0 + 20);
        }

        //draw systolic and diastolic
        g2.setStroke(new BasicStroke(3f));
        g2.setColor(Color.RED); //systolic
        drawLineSeries(g2, data, start, size, x0, x1, min, max, h, true);

        g2.setColor(Color.BLUE); //diastolic
        drawLineSeries(g2, data, start, size, x0, x1, min, max, h, false);

        //legend
        g2.setColor(Color.RED);
        g2.drawString("Systolic", w - 120, 20);
        g2.setColor(Color.BLUE);
        g2.drawString("Diastolic", w - 120, 35);
    }

    //draw line series for systolic or diastolic values
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
            int x = x0 + i * (x1 - x0) / Math.max(1, (size - 1));

            double value = systolic
                    ? data.get(start + i).getSystole()
                    : data.get(start + i).getDiastole();

            int y = scale(value, min, max, h);

            g2.drawLine(prevX, prevY, x, y);

            prevX = x;
            prevY = y;
        }
    }

    //scale a blood pressure value to panel y-coordinate
    private int scale(double value, double min, double max, int height) {
        double normalized = (value - min) / (max - min);
        return (int) ((height - 2 * PAD) * (1 - normalized)) + PAD;
    }
}
