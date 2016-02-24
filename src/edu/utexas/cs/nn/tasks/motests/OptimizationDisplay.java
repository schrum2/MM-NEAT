package edu.utexas.cs.nn.tasks.motests;

import edu.utexas.cs.nn.graphics.DrawingPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class OptimizationDisplay {

    DrawingPanel panel;
    public static int EDGE = 500;
    public static int BORDER = 50;
    ArrayList<Point2D.Double> points;
    ArrayList<Point2D.Double> front;

    public OptimizationDisplay() {
        panel = new DrawingPanel(EDGE + 2 * BORDER, EDGE + 2 * BORDER, "Function Optimization");
        points = new ArrayList<Point2D.Double>();
        front = new ArrayList<Point2D.Double>();
    }

    public void clear() {
        Graphics g = panel.getGraphics();
        g.setColor(Color.WHITE);
        g.clearRect(0, 0, EDGE + 2 * BORDER, EDGE + 2 * BORDER);
        //g.drawLine(BORDER, BORDER+EDGE, BORDER, BORDER);
        //g.drawLine(BORDER, BORDER+EDGE, BORDER+EDGE, BORDER+EDGE);
        drawAll();
        points = new ArrayList<Point2D.Double>();
    }

    public void addPoint(double x, double y, boolean addToFront) {
        //System.out.println("display:" + x+" "+y);
        if (addToFront) {
            front.add(new Point2D.Double(x, -1 * y));
        } else {
            points.add(new Point2D.Double(x, -1 * y));
        }
    }

    private int scale(double i, double offset, double range) {
        return (int) (BORDER + (EDGE * ((i - offset) / range)));
    }

    private void drawAll() {

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        ArrayList<Point2D.Double> combo = new ArrayList<Point2D.Double>();
        combo.addAll(points);
        combo.addAll(front);
        for (Point2D.Double p : combo) {
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
        }

        minY = Math.floor(minY);
        maxY = Math.ceil(maxY);
        minX = Math.floor(minX);
        maxX = Math.ceil(maxX);

        double[] scale = new double[]{maxX - minX, maxY - minY};
        double[] offset = new double[]{minX, minY};

        Graphics g = panel.getGraphics();
        g.drawString("" + (-maxY), 5, scale(maxY, offset[1], scale[1]));
        g.drawString("" + (-minY), 5, scale(minY, offset[1], scale[1]));

        g.drawString("" + maxX, scale(maxX, offset[0], scale[0]), EDGE + 2 * BORDER - 5);
        g.drawString("" + minX, scale(minX, offset[0], scale[0]), EDGE + 2 * BORDER - 5);

        g.setColor(Color.RED);
        for (Point2D.Double p : front) {
            int x = scale(p.x, offset[0], scale[0]);
            int y = scale(p.y, offset[1], scale[1]);
            g.fillOval(x, y, 1, 1);
        }

        g.setColor(Color.WHITE);
        for (Point2D.Double p : points) {
            int x = scale(p.x, offset[0], scale[0]);
            int y = scale(p.y, offset[1], scale[1]);
            g.fillOval(x, y, 3, 3);
        }
    }
}
