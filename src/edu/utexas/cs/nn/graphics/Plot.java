/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class Plot {

    public static final int BROWSE_DIM = 300;
    public static final int OFFSET = 20;
    public static final int EDGE = 16;
    public static final int TOP = 56;
    public static final int OVAL_DIM = 5;

    public static void linePlot(DrawingPanel panel, double min, double max, ArrayList<Double> scores, Color color) {
        Graphics g = panel.getGraphics();
        g.setColor(Color.black);
        g.drawLine(OFFSET, OFFSET, OFFSET, BROWSE_DIM - OFFSET);
        g.drawLine(OFFSET, BROWSE_DIM - OFFSET, BROWSE_DIM - OFFSET, BROWSE_DIM - OFFSET);
        double last = scores.get(0);
        double maxRange = Math.max(max, max - min);
        double lowerMin = Math.min(0, min);
        for (int i = 1; i < scores.size(); i++) {
            g.setColor(color);
            //    g.fillRect(OFFSET + scale((double) i, (double) scores.size()), OFFSET + invert(scores.get(i), max), 1, 1);
            int x1 = OFFSET + scale((double) (i - 1), (double) scores.size(), 0);
            int y1 = OFFSET + invert(last, maxRange, lowerMin);
            int x2 = OFFSET + scale((double) i, (double) scores.size(), 0);
            int y2 = OFFSET + invert(scores.get(i), maxRange, lowerMin);
            g.drawLine(x1, y1, x2, y2);
            g.setColor(Color.black);
            g.drawString("" + max, OFFSET / 2, OFFSET / 2);
            g.drawString("" + lowerMin, OFFSET / 2, BROWSE_DIM - (OFFSET / 2));
            last = scores.get(i);
        }
    }

    public static int scale(double x, double max, double min) {
        return (int) (((x - min) / max) * (BROWSE_DIM - (2 * OFFSET)));
    }

    public static int invert(double y, double max, double min) {
        return (BROWSE_DIM - (2 * OFFSET)) - scale(y, max, min);
    }
}
