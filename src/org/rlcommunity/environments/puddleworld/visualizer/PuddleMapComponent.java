/*
 *  Copyright 2009 Brian Tanner.
 *
 *  brian@tannerpages.com
 *  http://research.tannerpages.com
 *
 *  This source file is from one of:
 *  {rl-coda,rl-glue,rl-library,bt-agentlib,rl-viz}.googlecode.com
 *  Check out http://rl-community.org for more information!
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.rlcommunity.environments.puddleworld.visualizer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.rlcommunity.environments.puddleworld.Puddle;
import org.rlcommunity.environments.puddleworld.PuddleGen;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;
import rlVizLib.visualization.SelfUpdatingVizComponent;
import rlVizLib.visualization.VizComponentChangeListener;

public class PuddleMapComponent implements SelfUpdatingVizComponent, Observer {

    private final PuddleWorldVisualizer theVisualizer;
    private VizComponentChangeListener theChangeListener;
    private final Vector<Puddle> thePuddles = PuddleGen.makePuddles();

    public PuddleMapComponent(PuddleWorldVisualizer theVisualizer) {
        this.theVisualizer = theVisualizer;
        theVisualizer.getTheGlueState().addObserver(this);
    }

    public void render(Graphics2D g) {



//        AffineTransform theScaleTransform = new AffineTransform();
//        theScaleTransform.scale(4.0d, 4.0d);
//        AffineTransform xtransform = g.getTransform();
//        xtransform.concatenate(theScaleTransform);
//        g.setTransform(xtransform);

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
                    Rectangle2D thisRect = new Rectangle2D.Double(x, y, increment, increment);
                    g.fill(thisRect);
                }




            }
        }

    }

    public void setVizComponentChangeListener(VizComponentChangeListener theChangeListener) {
        this.theChangeListener = theChangeListener;
    }
    boolean everDrawn = false;

    public void update(Observable o, Object arg) {
        if (!everDrawn) {
            theChangeListener.vizComponentChanged(this);
            everDrawn = true;
        }
    }
}
