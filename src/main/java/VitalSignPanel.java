import javax.swing.*;
import java.awt.*;
import java.util.List;
import AllVitalSigns.VitalSign;

public class VitalSignPanel extends JPanel {

    private List<? extends VitalSign> data;
    private String title;
    private int maxPoints = 30;

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

        int w = getWidth();
        int h = getHeight();

        g2.drawString(title, 10, 15);

        int size = Math.min(data.size(), maxPoints);
        int start = data.size() - size;

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (int i = start; i < data.size(); i++) {
            double v = data.get(i).getValue();
            min = Math.min(min, v);
            max = Math.max(max, v);
        }

        int prevX = 30;
        int prevY = scale(data.get(start).getValue(), min, max, h);

        for (int i = 1; i < size; i++) {
            int x = 30 + i * (w - 60) / size;
            int y = scale(data.get(start + i).getValue(), min, max, h);

            g2.drawLine(prevX, prevY, x, y);

            prevX = x;
            prevY = y;
        }
    }

    private int scale(double value, double min, double max, int height) {
        if (max - min == 0) return height / 2;

        double normalized = (value - min) / (max - min);
        return (int) ((height - 40) * (1 - normalized)) + 20;
    }
}
