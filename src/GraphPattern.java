import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraphPattern extends JPanel {
    private GraphData data;
    public GraphData.TEC tec;
    private Fade fade;
    private Color color;
    private float opacity = 0;

    public GraphPattern(GraphData data, GraphData.TEC tec) {
        this.data = data;
        this.tec = tec;
        this.color = data.rainbow[tec.index%data.rainbow.length];
        this.setDimensions();
        this.setBackground(new Color(0,0,0,0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        this.setDimensions();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(color);
        if (opacity > 0) {
            //g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        }

        for (int i = 0; i < tec.patterns.size(); i++) {
            paintPattern(tec.patterns.get(i),g2);
        }
    }

    private void paintPattern(List<Point> pattern, Graphics2D g2) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = 0, maxY = 0;
        int margin = 0;//data.diameter / 2;

        if (data.currIndex == tec.index + 1 || data.currIndex == data.tecs.size()) {
            g2.setColor(color);
        } else {
            g2.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),100));
        }

        for (int i = 0; i < pattern.size(); i++) {
            var p = pattern.get(i);

            int x = p.x - margin;
            int y = p.y - margin;
            int x1 = (int) (x * data.scaleX + data.padding*2);
            int y1 = (int) ((data.maxY - y) * data.scaleY + data.padding);

            g2.fillOval(x1, y1, data.diameter, data.diameter);

            maxX = Math.max(maxX, x1);
            maxY = Math.max(maxY, y1);
            minX = Math.min(minX, x1);
            minY = Math.min(minY, y1);
        }

        //System.out.println("Index: " + tec.index + ", Current: " + data.currIndex);

        if (data.currIndex == tec.index + 1) {
            //g2.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),50));
            g2.drawRoundRect(minX-margin,minY-margin,maxX-minX+data.diameter*2,maxY-minY+data.diameter*2,2,2);
            //g2.setColor(color);
        }
    }

    public void setDimensions() {
        this.setBounds(0,0,data.width-data.padding,data.height-data.padding*3);
    }

    public void fadeIn(){
        if(fade != null) {
            fade.interrupt();
        }
        //fade = new Fade(this);
        this.setVisible(true);
    }

    class Fade implements Runnable {
        private Thread runner;
        private GraphPattern pattern;

        public boolean interrupt = false;

        public Fade(GraphPattern pattern) {
            this.pattern = pattern;
            runner = new Thread(this);
            runner.start();
        }

        public void interrupt() {
            interrupt = true;
            runner.interrupt();
        }

        public void run() {
            opacity = 0.01f;
            pattern.setVisible(true);
            while (!interrupt) {
                pattern.repaint();
                opacity += 0.05f;

                if (opacity > 0.3f) {
                    opacity = 0;
                    pattern.repaint();
                    this.interrupt();
                    return;
                }

                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    this.interrupt();
                    return;
                }
            }
        }
    }
}
