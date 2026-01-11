package UI;

import AllVitalSigns.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ECGplot extends JPanel {

    private List<? extends ECG> data;

    // ECG-specific controls
    private int timeWindowSeconds = 10;     // show last N seconds
    private int samplesPerSecond = 100;     // expected sampling rate
    private double voltageRange = 1.0;      // y-range is [-voltageRange, +voltageRange]

    private final int PAD = 40;

    public ECGplot() {
        setPreferredSize(new Dimension(900, 150));
        setBackground(Color.WHITE);
        setOpaque(true);
    }

    /** Pass your ECG history list here */
    public void updateData(List<? extends ECG> data) {
        this.data = data;
        repaint();
    }

    /** e.g., 5, 10, 15 seconds */
    public void setTimeWindowSeconds(int seconds) {
        this.timeWindowSeconds = Math.max(1, seconds);
        repaint();
    }

    /** e.g., 50–250 typical for ECG simulations */
    public void setSamplesPerSecond(int sps) {
        this.samplesPerSecond = Math.max(1, sps);
        repaint();
    }

    /** If your ECG is normalized -1..1, setVoltageRange(1.0) is perfect */
    public void setVoltageRange(double range) {
        this.voltageRange = Math.max(0.1, range);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (data == null || data.size() < 2) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        int x0 = PAD;
        int y0 = h - PAD;
        int x1 = w - PAD;
        int y1 = PAD;

        // Plot area sizes
        int plotW = Math.max(1, x1 - x0);
        int plotH = Math.max(1, y0 - y1);
        int midY = y1 + plotH / 2;

        // Number of points to draw = last (timeWindowSeconds * samplesPerSecond)
        int maxPoints = timeWindowSeconds * samplesPerSecond;
        int size = Math.min(data.size(), maxPoints);
        int start = data.size() - size;
        if (size < 2) return;

        // ----- Axes -----
        g2.setColor(Color.BLACK);
        g2.drawLine(x0, y0, x1, y0); // X axis
        g2.drawLine(x0, y0, x0, y1); // Y axis

        // ----- Y ticks (fixed range: -range .. +range) -----
        int yTicks = 4; // gives 5 tick marks
        for (int i = 0; i <= yTicks; i++) {
            double frac = (double) i / yTicks;                 // 0..1
            double value = -voltageRange + frac * (2 * voltageRange);

            int y = (int) Math.round(midY - value * (plotH / 2.0) / voltageRange);

            g2.drawLine(x0 - 5, y, x0 + 5, y);
            g2.drawString(String.format("%.2f", value), 5, y + 5);
        }

        // Midline (baseline)
        g2.setColor(new Color(220, 220, 220));
        g2.drawLine(x0, midY, x1, midY);

        // ----- X ticks in seconds -----
        g2.setColor(Color.BLACK);
        int xTicks = 5;
        for (int i = 0; i <= xTicks; i++) {
            int x = x0 + i * plotW / xTicks;
            int sec = i * timeWindowSeconds / xTicks;

            g2.drawLine(x, y0 - 5, x, y0 + 5);
            g2.drawString(sec + "s", x - 10, y0 + 20);
        }

        // ----- ECG line -----
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(Color.BLACK);

        double dx = (double) plotW / (size - 1);

        int prevX = x0;
        int prevY = scaleVoltageToY(data.get(start).getVoltage(), midY, plotH);

        for (int i = 1; i < size; i++) {
            int x = (int) Math.round(x0 + i * dx);
            int y = scaleVoltageToY(data.get(start + i).getVoltage(), midY, plotH);
            g2.drawLine(prevX, prevY, x, y);

            prevX = x;
            prevY = y;
        }

        // Optional label
        g2.drawString("ECG (last " + timeWindowSeconds + "s)", x0 + 5, y1 - 10);
    }

    private int scaleVoltageToY(double v, int midY, int plotH) {
        // clamp to keep it on-screen if signal spikes
        if (v > voltageRange) v = voltageRange;
        if (v < -voltageRange) v = -voltageRange;

        double yScale = (plotH / 2.0) / voltageRange;
        return (int) Math.round(midY - v * yScale);
    }
}

/*package UI;

import AllVitalSigns.ECG;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ECGplot extends JPanel {

    private int timeInterval = 10;
    private int samplesPerSecond = 100;
    private double mVrange = 0.5;

    private List<? extends ECG> data; // this is what we draw

    public ECGplot() {
        setPreferredSize(new Dimension(900, 150));
        setBackground(Color.WHITE);
        setOpaque(true);
    }

    public void updateData(List<? extends ECG> data) {
        this.data = data;
        repaint();
    }

    public void setTimeInterval(int seconds) {
        this.timeInterval = Math.max(1, seconds);
        repaint();
    }

    public void setSamplesPerSecond(int sps) {
        this.samplesPerSecond = Math.max(1, sps);
        repaint();
    }

    public void setVoltage(double voltage) {
        this.mVrange = Math.max(0.1, voltage);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            int left = 10, right = 10, top = 20, bottom = 10;
            int plotW = Math.max(1, w - left - right);
            int plotH = Math.max(1, h - top - bottom);
            int midY = top + plotH / 2;

            // baseline
            g2.setColor(new Color(230, 230, 230));
            g2.drawLine(left, midY, left + plotW, midY);

            if (data == null || data.size() < 2) return;

            int maxSamples = timeInterval * samplesPerSecond;
            int end = data.size();
            int start = Math.max(0, end - maxSamples);
            int n = end - start;
            if (n < 2) return;

            double dx = (double) plotW / (n - 1);
            double yScale = (plotH / 2.0) / mVrange;

            g2.setColor(Color.BLACK);

            int prevX = left;
            int prevY = (int) Math.round(midY - data.get(start).getVoltage() * yScale);

            for (int i = 1; i < n; i++) {
                int x = (int) Math.round(left + i * dx);
                double v = data.get(start + i).getVoltage();
                int y = (int) Math.round(midY - v * yScale);

                g2.drawLine(prevX, prevY, x, y);
                prevX = x;
                prevY = y;
            }

            g2.setColor(new Color(80, 80, 80));
            g2.drawString("Last " + timeInterval + "s", left + 5, top - 5);

        } finally {
            g2.dispose();
        }
    }
}
 */

