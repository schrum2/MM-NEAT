/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.rlglue.puddleworld;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.util.CombinatoricUtilities;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Vector;
import org.rlcommunity.environments.puddleworld.Puddle;

/**
 *
 * @author Jacob Schrum
 */
public final class PuddleWorldViewer {

    public static PuddleWorldViewer current = null;
    public static int colorCode = 0;
    public static final int HEIGHT = 500;
    public static final int WIDTH = 500;
    public static final String TITLE = "Puddle World";
    public static final int DOT_SIZE = 4;
    public static final int BUFFER = 10;
    public DrawingPanel panel;
    public int lastX;
    public int lastY;
    private final boolean erase;

    public PuddleWorldViewer() {
        erase = Parameters.parameters.booleanParameter("erasePWTrails");
        panel = new DrawingPanel(WIDTH, HEIGHT, TITLE);
        panel.setLocation(TWEANN.NETWORK_VIEW_DIM, 0);
        reset(true);
        current = this;
    }

    public void reset() {
        reset(false);
    }

    public void reset(boolean first) {
        lastX = -1;
        lastY = -1;
        // Erase
        if (first || erase) {
            Graphics2D g = panel.getGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setColor(Color.BLACK);
            g.drawRect(BUFFER, BUFFER, WIDTH - 2 * BUFFER, HEIGHT - 2 * BUFFER);
        }
    }

    public void visit(Point2D p) {
        visit(p.getX(), p.getY());
    }

    public void visit(double x, double y) {
        Graphics2D g = panel.getGraphics();
        // Update previous point
        if (lastX >= 0) {
            g.setColor(CombinatoricUtilities.colorFromInt(colorCode)); // visited
            g.fillOval(lastX, lastY, DOT_SIZE, DOT_SIZE);
        } else {
            colorCode++;
        }
        // New point
        int newX = mapX(x, true);
        int newY = mapY(y, true);
        if (lastX >= 0) {
            g.drawLine(
                    lastX + (DOT_SIZE / 2),
                    lastY + (DOT_SIZE / 2),
                    newX + (DOT_SIZE / 2),
                    newY + (DOT_SIZE / 2));
        }
        lastX = newX;
        lastY = newY;
        g.setColor(Color.RED); // visited
        g.fillOval(lastX, lastY, DOT_SIZE, DOT_SIZE);
    }

    private static int mapX(double x, boolean dot) {
        return (int) (BUFFER + (x * (WIDTH - (2 * BUFFER))) - (dot ? DOT_SIZE / 2 : 0));
    }

    private static int mapY(double y, boolean dot) {
        double actualH = (HEIGHT - (2 * BUFFER));
        return (int) (BUFFER + (actualH - (y * actualH)) - (dot ? DOT_SIZE / 2 : 0));
    }

    private int scaleX(double w) {
        return (int) (w * (WIDTH - (2 * BUFFER)));
    }

    private int scaleY(double h) {
        return (int) (h * (HEIGHT - (2 * BUFFER)));
    }

    public void drawGoal(Rectangle2D goalRect) {
        Graphics2D g = panel.getGraphics();
        g.setColor(Color.GREEN);
        int x = mapX(goalRect.getX(), false);
        int y = mapY(goalRect.getY(), false);
        int w = scaleX(goalRect.getWidth());
        int h = scaleY(goalRect.getHeight());
        g.drawRect(x, y - h, w, h);
    }

    // Stolen from the render method of the original visualizer
    public void drawPuddles(Vector<Puddle> thePuddles) {
        Graphics2D g = panel.getGraphics();
        double increment = .0025d;
        for (double x = 0.0d; x <= 1.0d; x += increment) {
            for (double y = 0.0d; y <= 1.0d; y += increment) {
                Point2D thisPoint = new Point2D.Double(x + increment / 2.0d, y + increment / 2.0d);
                float thisPenalty = 0.0f;
                for (Puddle puddle : thePuddles) {
                    thisPenalty += puddle.getReward(thisPoint);
                }
                //If we are in penalty region, draw the puddle
                if (thisPenalty < 0.0d) {
                    //empirically have determined maxpenalty = -80
                    float scaledPenalty = thisPenalty / (-80.0f);
                    //Going to sqrt the penalty to bias it towards 1
                    scaledPenalty = (float) Math.sqrt(scaledPenalty);
                    //Now we have a number in 0/1
                    Color scaledColor = new Color(scaledPenalty, 1.0f, 1.0f, .75f);
                    g.setColor(scaledColor);
                    Rectangle2D thisRect = new Rectangle2D.Double(mapX(x, false), mapY(y, false) - scaleY(increment), scaleX(increment), scaleY(increment));
                    g.fill(thisRect);
                }
            }
        }

    }
//    public void close() {
//        panel.dispose();
//        current = null;
//    }
}
