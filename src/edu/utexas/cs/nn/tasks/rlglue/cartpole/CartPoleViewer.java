/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.rlglue.cartpole;

import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.TWEANN;
import java.awt.*;
import java.awt.geom.AffineTransform;
import rlVizLib.utilities.UtilityShop;

/**
 *
 * @author Jacob Schrum
 */
public class CartPoleViewer {

    public static CartPoleViewer current = null;
    public static final int HEIGHT = 500;
    public static final int WIDTH = 500;
    public static final String TITLE = "CartPole";
    public DrawingPanel panel;
    private double poleLength = .3d; //30% of the screen long

    public CartPoleViewer() {
        panel = new DrawingPanel(WIDTH, HEIGHT, TITLE);
        panel.setLocation(TWEANN.NETWORK_VIEW_DIM, 0);
        reset();
        current = this;
    }

    public void reset() {
        Graphics2D g = panel.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        renderTrack(g);
    }

    /**
     * Taken from CartPoleTrackComponent
     *
     * @param g
     */
    public void renderTrack(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 1, 1);
        //SET COLOR
        g.setColor(Color.BLACK);
        //DRAW 12 Lines with blue ball equalizers.

        //AffineTransform saveAT = g.getTransform();
        //g.scale(.01, .01);
        g.drawLine((int) (0.1 * WIDTH), (int) (0.88 * HEIGHT), (int) (0.9 * WIDTH), (int) (0.88 * HEIGHT));
        //g.setTransform(saveAT);
    }

    /**
     * Modified from CartPoleCartComponent
     *
     * @param g
     */
    public void renderCart(double leftCartBound, double rightCartBound,
            double leftAngleBound, double rightAngleBound,
            double x, double x_dot,
            double theta, double theta_dot) {
        Graphics2D g = panel.getGraphics();
        //SET COLOR
        g.setColor(Color.BLACK);

        //AffineTransform saveAT = g.getTransform();
//        double scale = .1;
        double inverseScale = HEIGHT;
        double eightyPercent = .8d * inverseScale;
        double tenPercent = .1d * inverseScale;
        double fivePercent = .05d * inverseScale;
        double twentyPercent = .2d * inverseScale;
        //g.scale(scale, scale);
        int transX = (int) (UtilityShop.normalizeValue(x, leftCartBound, rightCartBound) * (eightyPercent) + tenPercent);
        int transY = (int) eightyPercent;

        g.setColor(Color.blue);
        Rectangle carRect = new Rectangle((int) (transX - tenPercent), transY, (int) twentyPercent, (int) fivePercent);
        //System.out.println(carRect);
        g.fill(carRect);
        drawWheels(g, carRect);


        //Draw the pole
        Stroke stroke = new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g.setStroke(stroke);
        theta = theta - 2.0 * Math.PI / 4.0;
        int x2 = transX + (int) (inverseScale * poleLength * Math.cos(theta));
        int y2 = transY + (int) (inverseScale * poleLength * Math.sin(theta));
        g.setColor(Color.BLACK);
        g.drawLine(transX, transY, x2, y2);

        double minAngle = leftAngleBound - 2.0 * Math.PI / 4.0;
        double maxAngle = rightAngleBound - 2.0 * Math.PI / 4.0;

        //Draw the failure lines
        int failLeftX = transX + (int) (inverseScale * poleLength * Math.cos(minAngle));
        int failLeftY = transY + (int) (inverseScale * poleLength * Math.sin(minAngle));
        g.setColor(Color.RED);
        g.drawLine(transX, transY, failLeftX, failLeftY);
        int failRightX = transX + (int) (inverseScale * poleLength * Math.cos(maxAngle));
        int failRightY = transY + (int) (inverseScale * poleLength * Math.sin(maxAngle));
        g.setColor(Color.RED);
        g.drawLine(transX, transY, failRightX, failRightY);

        //g.setTransform(saveAT);
    }

    private void drawWheels(Graphics2D g, Rectangle carRect) {
        g.setColor(Color.red);

        double carMidY = carRect.getCenterY();
        double carX1 = carRect.getMinX() + carRect.width / 4.0d;
        double carX2 = carRect.getMaxX() - carRect.width / 4.0d;
        double wheelRad = carRect.height;
        g.fillOval((int) (carX1 - wheelRad / 2.0d), (int) (carMidY), (int) wheelRad, (int) wheelRad);
        g.fillOval((int) (carX2 - wheelRad / 2.0d), (int) (carMidY), (int) wheelRad, (int) wheelRad);
    }
}
