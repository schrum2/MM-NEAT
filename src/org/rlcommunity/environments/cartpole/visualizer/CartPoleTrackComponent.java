package org.rlcommunity.environments.cartpole.visualizer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.Observer;
import rlVizLib.visualization.SelfUpdatingVizComponent;
import rlVizLib.visualization.VizComponentChangeListener;

public class CartPoleTrackComponent implements SelfUpdatingVizComponent, Observer {

    private CartPoleVisualizer cartVis = null;
    boolean drawn = false;

    public CartPoleTrackComponent(CartPoleVisualizer cartpoleVisualizer) {
        cartVis = cartpoleVisualizer;
        cartpoleVisualizer.getTheGlueState().addObserver(this);

    }

    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 1, 1);
        //SET COLOR
        g.setColor(Color.BLACK);
        //DRAW 12 Lines with blue ball equalizers.

        AffineTransform saveAT = g.getTransform();
        g.scale(.01, .01);
        g.drawLine(10, 88, 90, 88);
        g.setTransform(saveAT);
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
            if (!drawn) {
                theChangeListener.vizComponentChanged(this);
                drawn = true;
            }
        }
    }
}
