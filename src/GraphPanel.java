import java.awt.*;
import javax.swing.JPanel;

public class GraphPanel extends JPanel {
    private Color gridColor = new Color(200, 200, 200, 200);
    private int diameter = 4;
    private int numberYDivisions = 10;
    private GraphData data;

    public GraphPanel(GraphData data) {
        this.data = data;
        setDimensions();
        setScale();
        this.setBackground(Color.darkGray);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(data.padding*2, data.padding, getWidth() - (3 * data.padding), getHeight() - 3 * data.padding);
        g2.setColor(Color.BLACK);

        // create hatch marks and grid lines for y axis.
        for (int i = 0; i < numberYDivisions + 1; i++) {
            int x0 = data.padding*2;
            int x1 = diameter + data.padding*2;
            int y0 = getHeight() - ((i * (getHeight() - data.padding * 3)) / numberYDivisions + data.padding*2);
            int y1 = y0;
            if (data.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(data.padding*2 + 1 + diameter, y0, getWidth() - data.padding, y1);
                g2.setColor(Color.WHITE);
                String yLabel = ((int) ((data.minY + (data.maxY - data.minY) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.setColor(Color.BLACK);
            g2.drawLine(x0, y0, x1, y1);
        }

        // and for x axis
        for (int i = 0; i < data.size(); i++) {
            if (data.size() > 1 && (i % ((int) ((data.size() / 20.0)) + 1)) == 0) {
                g2.setColor(Color.BLACK);
                int x0 = i * (getWidth() - data.padding * 3) / (data.size() - 1) + data.padding*2;
                int x1 = x0;
                int y0 = getHeight() - data.padding*2;
                int y1 = y0 - diameter;
                g2.drawLine(x0, y0, x1, y1);
                g2.setColor(gridColor);
                g2.drawLine(x0, getHeight() - data.padding*2 - 1 - diameter, x1, data.padding);
                g2.setColor(Color.WHITE);
                String xLabel = i + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(xLabel);
                g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
            }
        }

        // create x and y axes
        g2.setColor(Color.BLACK);
        g2.drawLine(data.padding*2, getHeight() - data.padding*2, data.padding*2, data.padding);
        g2.drawLine(data.padding*2, getHeight() - data.padding*2, getWidth() - data.padding, getHeight() - data.padding*2);
    }

    public void setDimensions() {
        this.setBounds(0,0,data.width-data.padding+10,data.height-data.padding*5);
    }

    public void setScale() {
        data.setScale(data.width-data.padding,data.height-data.padding*3);
    }

}
