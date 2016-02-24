/*
 Copyright 2007 Brian Tanner
 http://rl-library.googlecode.com/
 brian@tannerpages.com
 http://brian.tannerpages.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.rlcommunity.environments.mountaincar.visualizer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import rlVizLib.utilities.UtilityShop;
import rlVizLib.visualization.SelfUpdatingVizComponent;
import rlVizLib.visualization.VizComponentChangeListener;

public class MountainVizComponent implements SelfUpdatingVizComponent, Observer {

    private MountainCarVisualizer theVizualizer = null;
    private Vector<Double> theHeights = null;
    private double theGoalPosition = 0.0d;
    private double theGoalHeight = 0.0d;
    private VizComponentChangeListener theChangeListener;
    boolean everDrawn = false;

    public MountainVizComponent(MountainCarVisualizer theVizualizer) {
        this.theVizualizer = theVizualizer;
        theVizualizer.getTheGlueState().addObserver(this);
    }

    public void render(Graphics2D g) {
        if (!everDrawn) {
            theGoalPosition = theVizualizer.getGoalPosition();
            Vector<Double> tempVec = new Vector<Double>();
            tempVec.add(theGoalPosition);

            Vector<Double> returnVector = theVizualizer.getHeightsForPositions(tempVec);
            theGoalHeight = returnVector.get(0);
            everDrawn = true;

        }

        AffineTransform theScaleTransform = new AffineTransform();

        //Draw a rectangle
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 1, 1);

        double scaleFactor = 1000.0d;
        theScaleTransform.scale(1.0d / scaleFactor, 1.0d / scaleFactor);
        AffineTransform origTransform = g.getTransform();

        AffineTransform x = g.getTransform();
        x.concatenate(theScaleTransform);
        g.setTransform(x);


        theHeights = theVizualizer.getSampleHeights();

        double sizeEachComponent = 1.0 / (double) theHeights.size();


        g.setColor(Color.BLACK);

        double lastX = 0.0d;
        double lastY = 1.0 - UtilityShop.normalizeValue(theHeights.get(0), theVizualizer.getMinHeight(), theVizualizer.getMaxHeight());
        for (int i = 1; i < theHeights.size(); i++) {
            double thisX = lastX + sizeEachComponent;
            double thisY = 1.0 - UtilityShop.normalizeValue(theHeights.get(i), theVizualizer.getMinHeight(), theVizualizer.getMaxHeight());

            Line2D thisLine = new Line2D.Double(scaleFactor * lastX, scaleFactor * lastY, scaleFactor * thisX, scaleFactor * thisY);

            lastX = thisX;
            lastY = thisY;

            g.draw(thisLine);
        }




        g.setTransform(origTransform);

        g.setColor(Color.GREEN);

        //to bring things back into the window
        double minPosition = theVizualizer.getMinValueForDim(0);
        double maxPosition = theVizualizer.getMaxValueForDim(0);


        double transX = UtilityShop.normalizeValue(theGoalPosition, minPosition, maxPosition);
        //need to get he actual height ranges
        double transY = UtilityShop.normalizeValue(
                theGoalHeight,
                theVizualizer.getMinHeight(),
                theVizualizer.getMaxHeight());
        transY = (1.0 - transY);

        double rectWidth = .05 / 4;
        double rectHeight = 0.1;
        Rectangle2D fillRect = new Rectangle2D.Double(transX - rectWidth / 2.0d, transY - rectHeight / 2.0d, rectWidth, rectHeight);
        g.fill(fillRect);
    }

    public void setVizComponentChangeListener(VizComponentChangeListener theChangeListener) {
        this.theChangeListener = theChangeListener;
    }

    public void update(Observable o, Object arg) {
        if (!everDrawn) {
            theChangeListener.vizComponentChanged(this);
        }

    }
}
