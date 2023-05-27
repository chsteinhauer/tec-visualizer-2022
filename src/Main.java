import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

public class Main {
    private static GraphPanel graph;
    private static GraphData data;
    private static GraphPlot patterns;


    public static void main(String[] args) {
        var path = args[0];

        SwingUtilities.invokeLater(() -> {
            try {
                File file = new File(path);
                data = compileGraphData(file);
                gui();
            } catch (Exception e) {
                System.out.println("fail: " + e.getMessage());
            }
        });
    }

    private static GraphPanel addGraph() {
        var panel = new GraphPanel(data);

        return panel;
    }

    private static JMenuBar addMenuBar() {
        //Creating the MenuBar and adding components
        JMenuBar bar = new JMenuBar();
        JMenu m1 = new JMenu("File");
        JMenu m2 = new JMenu("Help");
        bar.add(m1);
        bar.add(m2);
        JMenuItem m11 = new JMenuItem("Open");
        JMenuItem m22 = new JMenuItem("Save as");
        m1.add(m11);
        m1.add(m22);

        return bar;
    }

    private static JPanel addControls(GraphPlot p) {
        //Creating the panel at bottom and adding components

        // Slider
        JSlider slider = new JSlider(0,data.tecs.size(),0);
        p.setSlider(slider);
        slider.setPaintTicks(true);
        slider.setMajorTickSpacing(5);
        slider.setMinorTickSpacing(1);
        slider.setMaximumSize(new Dimension(500,10));
        slider.setBackground(Color.DARK_GRAY);
        // Play / pause button
        JButton play = new JButton("Play");
        play.addActionListener(e -> {
            if (!p.isPlaying()) {
                p.play();
                play.setText("Pause");
            } else {
                p.pause();
                play.setText("Play");
            }
        });
        // Reset button
        JButton reset = new JButton("Reset");
        reset.addActionListener(e -> p.reset());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(play);
        buttonPanel.add(reset);
        buttonPanel.setBackground(Color.DARK_GRAY);

        // Panel setup
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(buttonPanel);
        panel.add(slider);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.setBackground(Color.DARK_GRAY);

        return panel;
    }

    private static void gui() {
        JFrame frame = new JFrame("RTT Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(600, 500));
        frame.setPreferredSize(new Dimension(data.width,data.height));

        graph = addGraph();
        patterns = new GraphPlot(data);
        var controls = addControls(patterns);
        var bar = addMenuBar();

        var graphPane = new JLayeredPane();
        graphPane.setPreferredSize(new Dimension(data.width,data.height));
        graphPane.setBackground(Color.DARK_GRAY);

        graphPane.add(graph,1);
        graphPane.add(patterns,0);

        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.NORTH, bar);
        frame.getContentPane().add(BorderLayout.CENTER, graphPane);
        frame.getContentPane().add(BorderLayout.SOUTH, controls);
        frame.getContentPane().setBackground(Color.BLACK);

        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                Component c = (Component)evt.getSource();
                data.width = c.getWidth();
                data.height = c.getHeight();

                patterns.setDimensions();
                graph.setDimensions();
                graph.setScale();

                graphPane.setPreferredSize(new Dimension(data.width,data.height));
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        graphPane.setVisible(true);
    }

    private static GraphData compileGraphData(File file) throws Exception {
        return new GraphData(file);
    }
}