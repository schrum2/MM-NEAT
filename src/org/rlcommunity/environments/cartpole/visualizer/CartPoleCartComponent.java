package org.rlcommunity.environments.cartpole.visualizer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import java.util.Observable;
import java.util.Observer;
import rlVizLib.utilities.UtilityShop;
import rlVizLib.visualization.SelfUpdatingVizComponent;
import rlVizLib.visualization.VizComponentChangeListener;

public class CartPoleCartComponent implements SelfUpdatingVizComponent, Observer {

    private CartPoleVisualizer cartVis = null;
    private double poleLength = .3d; //30% of the screen long

    public CartPoleCartComponent(CartPoleVisualizer cartpoleVisualizer) {
        cartVis = cartpoleVisualizer;
        cartpoleVisualizer.getTheGlueState().addObserver(this);

    }

    public void render(Graphics2D g) {
        //SET COLOR
        g.setColor(Color.BLACK);

        AffineTransform saveAT = g.getTransform();
        double scale = .0001;
        double inverseScale = 1.0d / scale;
        double eightyPercent = .8d * inverseScale;
        double tenPercent = .1d * inverseScale;
        double fivePercent = .05d * inverseScale;
        double twentyPercent = .2d * inverseScale;
        g.scale(scale, scale);
        int transX = (int) (UtilityShop.normalizeValue(cartVis.currentXPos(), cartVis.getLeftCartBound(), cartVis.getRightCartBound()) * (eightyPercent) + tenPercent);
        int transY = (int) eightyPercent;

        g.setColor(Color.blue);
        Rectangle carRect = new Rectangle((int) (transX - tenPercent), transY, (int) twentyPercent, (int) fivePercent);
        g.fill(carRect);
        drawWheels(g, carRect);


        //Draw the pole
        Stroke stroke = new BasicStroke(20.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g.setStroke(stroke);
        int x2 = transX + (int) (inverseScale * poleLength * Math.cos(cartVis.getAngle()));
        int y2 = transY + (int) (inverseScale * poleLength * Math.sin(cartVis.getAngle()));
        g.setColor(Color.BLACK);
        g.drawLine(transX, transY, x2, y2);


        //Draw the failure lines
        int failLeftX = transX + (int) (inverseScale * poleLength * Math.cos(cartVis.getMinAngle()));
        int failLeftY = transY + (int) (inverseScale * poleLength * Math.sin(cartVis.getMinAngle()));
        g.setColor(Color.RED);
        g.drawLine(transX, transY, failLeftX, failLeftY);
        int failRightX = transX + (int) (inverseScale * poleLength * Math.cos(cartVis.getMaxAngle()));
        int failRightY = transY + (int) (inverseScale * poleLength * Math.sin(cartVis.getMaxAngle()));
        g.setColor(Color.RED);
        g.drawLine(transX, transY, failRightX, failRightY);



        g.setTransform(saveAT);
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
    /**
     * This is the object (a renderObject) that should be told when this
     * component needs to be drawn again.
     */
    private VizComponentChangeListener theChangeListener;

    public void setVizComponentChangeListener(VizComponentChangeListener theChangeListener) {
        this.theChangeListener = theChangeListener;
    }

    /**
     * This will be called when TinyGlue steps.
     *
     * @param o
     * @param arg
     */
    public void update(Observable o, Object arg) {
        if (theChangeListener != null) {
            theChangeListener.vizComponentChanged(this);
        }
    }
}
