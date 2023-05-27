import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

public class GraphPlot extends JLayeredPane implements ActionListener {
    private GraphData data;
    private boolean isPlaying = false;
    private boolean triggerSlide = true;
    private Timer time;
    private Thread runner;
    private JSlider slider;

    public GraphPlot(GraphData data) {
        this.data = data;
        this.time = new Timer(500, this);

        setDimensions();
        setupLayers();
        this.setBackground(new Color(0,0,0,0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    private void setupLayers() {
        data.tecs.stream()
                //.filter(t -> t.index < freeze)
                .forEachOrdered(tec -> {
                    var p = new GraphPattern(data, tec);
                    this.add(p, tec.index);
                    p.setVisible(false);
                });
    }

    public void play() {
        isPlaying = true;
        time.start();
    }

    public void pause() {
        isPlaying = false;
        time.stop();
    }

    public void reset() {
        pause();

        var patterns = this.getComponents();
        for (int i = 0; i < data.currIndex; i++) {
            var pattern = (GraphPattern) patterns[i];
            pattern.setVisible(false);
        }

        setCurrentIndex(0,false);
    }

    public void slide(int index) {
        if (!triggerSlide) {
            triggerSlide = true;
            return;
        }
        pause();

        var min = Math.min(data.currIndex,index);
        var max = Math.max(data.currIndex,index);

        if (runner != null) runner.interrupt();

        runner = new Thread(() -> {
            var patterns = this.getComponents();
            for (int i = min; i < max; i++) {
                var pattern = (GraphPattern) patterns[i];
                if (i < index) {
                    pattern.fadeIn();
                } else {
                    pattern.setVisible(false);
                }
            }
        });

        runner.start();

        setCurrentIndex(index,false);
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setSlider(JSlider slider) {
        this.slider = slider;
        slider.addChangeListener(e -> {
            this.slide((slider.getValue()));

        });
    }

    public void setCurrentIndex(int index, boolean triggerSlide) {
        data.currIndex = index;
        this.triggerSlide = triggerSlide;
        slider.setValue(index);
        this.triggerSlide = true;
    }

    public void setDimensions() {
        this.setBounds(0,0,data.width-data.padding,data.height-data.padding*5);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(data.width,data.height);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isPlaying) return;

        if (data.currIndex < data.tecs.size()) {
            var pattern = this.getComponent(data.currIndex);
            ((GraphPattern)pattern).fadeIn();
            setCurrentIndex(data.currIndex+1,false);
        } else {
            pause();
        }
    }
}
