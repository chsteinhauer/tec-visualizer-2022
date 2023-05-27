import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphData {
    private final Pattern TEC_REGEX = Pattern.compile("T\\(P\\((?<p>.+?)\\),V\\((?<v>.+?)\\)\\)", Pattern.DOTALL);
    private final Pattern P_REGEX = Pattern.compile("p\\((?<p1>-?\\d+),(?<p2>-?\\d+)\\)", Pattern.DOTALL);
    private final Pattern V_REGEX = Pattern.compile("v\\((?<v1>-?\\d+),(?<v2>-?\\d+)\\)", Pattern.DOTALL);

    public final int diameter = 4, padding = 25;
    public double scaleX = 1.0, scaleY = 1.0;
    public final Color[] rainbow = {
            Color.blue,
            Color.yellow,
            Color.red,
            Color.green,
            Color.magenta,
            Color.cyan,
            Color.orange,
            Color.darkGray,
            Color.pink,
            Color.black,
    };

    public int width = 1000;
    public int height = 300;


    private class TECPoint {
        private int x;
        private int y;

        public Point transform(TECVector v) {
            return v.transform(x,y);
        }

        public TECPoint(String p1, String p2) {
            x = Integer.decode(p1);
            y = Integer.decode(p2);

            //System.out.println("Point: " + x + ", " + y);
        }

        public TECPoint(int p1, int p2) {
            x = p1;
            y = p2;
        }
    }

    private class TECVector {
        private int v1;
        private int v2;

        public Point transform(int x, int y) {
            var t1 = x+v1;
            var t2 = y+v2;
            setDimensions(t1,t2);

            return new Point(t1,t2);
        }

        public TECVector(String _v1, String _v2) {
            v1 = Integer.decode(_v1);
            v2 = Integer.decode(_v2);

            //System.out.println("Vector: " + _v1 + ", " + _v2);
        }
    }

    private int count = 0;
    public class TEC extends Component {
        private List<TECPoint> points = new ArrayList<>();
        private List<TECVector> vectors = new ArrayList<>();
        public List<List<Point>> patterns = new ArrayList<>();

        public int index;

        private void compile(String _points, String _vectors) {
            final Matcher mt = TEC_REGEX.matcher(_points);
            var tec = mt.find() ? new TEC(mt.group("p"),mt.group("v")) : null;

            if (tec != null) {
                points.addAll(tec.points);
            }

            final Matcher mp = P_REGEX.matcher(_points);
            while(mp.find()) {
                points.add(new TECPoint(mp.group("p1"), mp.group("p2")));
            }

            final Matcher mv = V_REGEX.matcher(_vectors);
            while(mv.find()) {
                var vec = new TECVector(mv.group("v1"), mv.group("v2"));
                vectors.add(vec);

                var _patterns = points.stream().map(p -> p.transform(vec)).toList();
                patterns.add(_patterns);
            }
        }

        public TEC(String _points, String _vectors) {
            compile(_points, _vectors);

            tecs.add(this);
            index = count++;
        }
    }

    public final List<TEC> tecs = new ArrayList<>();

    public int maxY, maxX, minX, minY, size = 0;

    public int currIndex = 0;

    public int size() {
        return size;
    }

    private void setDimensions(int x, int y) {
        maxX = Math.max(maxX, x);
        maxY = Math.max(maxY, y);
        minX = Math.min(minX, x);
        minY = Math.min(minY, y);
    }

    private void compileTECs(final String str) {
        final Matcher tec = TEC_REGEX.matcher(str);

        while(tec.find()) {
            var t = new TEC(tec.group("p"), tec.group("v"));
            size += t.points.size()*t.vectors.size();
        }
    }

    public GraphData(File file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));

        String str;
        while ((str = br.readLine()) != null) {
            compileTECs(str);
        }
    }

    public GraphData(String str) {
        compileTECs(str);
    }

    public void setScale(double widthX, double widthY) {
        scaleX = (widthX - 3 * padding)  / (maxX - minX);
        scaleY = (widthY - 3 * padding) / (maxY - minY);

        //System.out.println(scaleX + " " + scaleY);
        //System.out.println(maxX + " " + maxY + ", " + minX + " " + minY);
    }
}
