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
import java.awt.Image;
import java.awt.geom.AffineTransform;

import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import javax.imageio.ImageIO;
import rlVizLib.utilities.UtilityShop;
import rlVizLib.visualization.SelfUpdatingVizComponent;
import rlVizLib.visualization.VizComponentChangeListener;

public class CarOnMountainVizComponent implements SelfUpdatingVizComponent, Observer {

    private MountainCarVisualizer mcv = null;
    private boolean showAction = true;
    private VizComponentChangeListener theChangeListener;
    private Image carImageNeutral = null;
    private Image carImageLeft = null;
    private Image carImageRight = null;

    public CarOnMountainVizComponent(MountainCarVisualizer mc) {
        this.mcv = mc;
        mc.getTheGlueState().addObserver(this);
        URL carImageNeutralURL = CarOnMountainVizComponent.class.getResource("/images/auto.png");
        URL carImageLeftURL = CarOnMountainVizComponent.class.getResource("/images/auto_left.png");
        URL carImageRightURL = CarOnMountainVizComponent.class.getResource("/images/auto_right.png");
        try {
            carImageNeutral = ImageIO.read(carImageNeutralURL);
            carImageNeutral = carImageNeutral.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            carImageLeft = ImageIO.read(carImageLeftURL);
            carImageLeft = carImageLeft.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            carImageRight = ImageIO.read(carImageRightURL);
            carImageRight = carImageRight.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        } catch (IOException ex) {
            System.err.println("ERROR: Problem getting car image.");
        }

    }

    public void render(Graphics2D g) {
        g.setColor(Color.RED);
        AffineTransform saveAT = g.getTransform();
        g.scale(.005, .005);

        //to bring things back into the window
        double minPosition = mcv.getMinValueForDim(0);
        double maxPosition = mcv.getMaxValueForDim(0);

        int lastAction = mcv.getLastAction();

        double transX = UtilityShop.normalizeValue(this.mcv.getCurrentStateInDimension(0), minPosition, maxPosition);

        //need to get he actual height ranges
        double transY = UtilityShop.normalizeValue(
                this.mcv.getHeight(),
                mcv.getMinHeight(),
                mcv.getMaxHeight());
        transY = (1.0 - transY - .05);

        transX *= 200.0d;
        transY *= 200.0d;


        double theta = -mcv.getSlope() * 1.25;

        Image whichImageToDraw = carImageNeutral;
        if (lastAction == 0) {
            whichImageToDraw = carImageLeft;
        }
        if (lastAction == 2) {
            whichImageToDraw = carImageRight;
        }
        AffineTransform theTransform = AffineTransform.getTranslateInstance(transX - whichImageToDraw.getWidth(null) / 2.0d, transY - whichImageToDraw.getHeight(null) / 2.0d);
        theTransform.concatenate(AffineTransform.getRotateInstance(theta, whichImageToDraw.getWidth(null) / 2, whichImageToDraw.getHeight(null) / 2));
        g.drawImage(whichImageToDraw, theTransform, null);

        g.setTransform(saveAT);

    }

    public void setVizComponentChangeListener(VizComponentChangeListener theChangeListener) {
        this.theChangeListener = theChangeListener;
    }

    public void update(Observable o, Object arg) {
        if (theChangeListener != null) {
            theChangeListener.vizComponentChanged(this);
            mcv.updateAgentState(true);
        }
    }
}
