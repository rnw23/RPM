package UI;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import AllVitalSigns.VitalSign;

/**
 * JPanel for plotting vital sign data in real-time.
 */
public class VitalSignPanel extends JPanel {

    private List<? extends VitalSign> data; // List of vital sign data points to display
    private int maxPoints = 30; // Maximum number of points to display
    private final int PAD = 40;  // Padding around the chart edges

    // Optional fixed min/max values for the y-axis
    private Double fixedMin = null;
    private Double fixedMax = null;


    public VitalSignPanel() {
        setPreferredSize(new Dimension(350, 250));
    }

    //update the vital sign data and repaint the panel
    public void updateData(List<? extends VitalSign> data) {
        this.data = data;
        repaint(); //trigger paintComponent()
    }

    //set max number of data points to display
    public void setMaxPoints(int maxPoints) {
        this.maxPoints = Math.max(2, maxPoints);
        repaint();
    }

    //fix y-axis scale to a specific range
    public void setFixedRange(double min, double max) {
        this.fixedMin = min;
        this.fixedMax = max;
        repaint();
    }

    //draw chart
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (data == null || data.size() < 2) return; // if insufficient data do not draw

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON); // smooth lines

        int w = getWidth(); //panel width
        int h = getHeight(); //panel height

        //determine segment of data to display
        int size = Math.min(data.size(), maxPoints);
        int start = data.size() - size;

        //find min.max in current data window
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (int i = start; i < data.size(); i++) {
            double v = data.get(i).getValue();
            min = Math.min(min, v);
            max = Math.max(max, v);
        }

        //apply fixed point (if specified)
        if (fixedMin != null && fixedMax != null) {
            min = fixedMin;
            max = fixedMax;
        } else if (max - min < 0.01) {
            max += 1;
            min -= 1;
        }

        //chart boundaries
        int x0 = PAD;
        int y0 = h - PAD;
        int x1 = w - PAD;
        int y1 = PAD;

        //axes
        g2.drawLine(x0, y0, x1, y0);
        g2.drawLine(x0, y0, x0, y1);

        //yaxis labels
        int yTicks = 5;
        for (int i = 0; i <= yTicks; i++) {
            int y = y0 - i * (y0 - y1) / yTicks;
            double value = min + i * (max - min) / yTicks;

            g2.drawLine(x0 - 5, y, x0 + 5, y);
            g2.drawString(String.format("%.1f", value), 5, y + 5);
        }

        //xaxis labels
        int xTicks = 5;
        for (int i = 0; i <= xTicks; i++) {
            int x = x0 + i * (x1 - x0) / xTicks;
            int seconds = i * (maxPoints / xTicks);

            g2.drawLine(x, y0 - 5, x, y0 + 5);
            g2.drawString(seconds + "s", x - 10, y0 + 20);
        }

        //data line
        g2.setStroke(new BasicStroke(3f));

        int prevX = x0;
        int prevY = scale(data.get(start).getValue(), min, max, h);

        for (int i = 1; i < size; i++) {
            int x = x0 + i * (x1 - x0) / Math.max(1, (size - 1));
            int y = scale(data.get(start + i).getValue(), min, max, h);

            g2.drawLine(prevX, prevY, x, y); //connect point

            prevX = x;
            prevY = y;
        }
    }

    //convert a data value to pixel y-coordinate
    private int scale(double value, double min, double max, int height) {
        double normalized = (value - min) / (max - min);
        return (int) ((height - 2 * PAD) * (1 - normalized)) + PAD;
    }
}
