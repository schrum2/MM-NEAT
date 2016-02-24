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
package org.rlcommunity.environments.puddleworld;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.rlglue.puddleworld.PuddleWorldViewer;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 * This class manages all of the problem parameters, current state variables,
 * and state transition and reward dynamics.
 *
 * @author btanner
 */
public class PuddleWorldState {

    public static double finalPuddleScore = 0;
    public static double finalStepScore = 0;

    static Point2D getDefaultPosition() {
        return new Point2D.Double(.1d, .1d);
    }
    private Point2D agentPosition = new Point2D.Double(.1d, .1d);
    private final Vector<Puddle> thePuddles = PuddleGen.makePuddles();
    public final Rectangle2D worldRect = new Rectangle2D.Double(0, 0, 1, 1);
    final public Point2D defaultInitPosition = new Point2D.Double(.2d, .5d);
    final private double goalSize = .05d;
    public final Rectangle2D goalRect = new Rectangle2D.Double(worldRect.getMaxX() - goalSize, worldRect.getMaxY() - goalSize, goalSize, goalSize);
    final private double agentSpeed = .05;
    final public double rewardPerStep = -1.0d;
    final public double rewardAtGoal = 0.0d;
    private Random randomGenerator;
    //These are configurable
    private boolean randomStarts = false;
    private double transitionNoise = 0.0d;
    private int lastAction = 0;
    private PuddleWorldViewer viewer = null;

    public PuddleWorldState(boolean randomStartStates, double transitionNoise, long randomSeed) {
        System.out.println("New Puddle World State");
        if (CommonConstants.watch) {
            if (PuddleWorldViewer.current == null) {
                System.out.println("New PuddleWorldViewer");
                viewer = new PuddleWorldViewer();
                viewer.drawGoal(goalRect);
                viewer.drawPuddles(thePuddles);
            } else {
                System.out.println("Same PuddleWorldViewer");
                PuddleWorldViewer.current.reset(true);
                viewer = PuddleWorldViewer.current;
            }
        }

        this.randomStarts = randomStartStates;
        this.transitionNoise = transitionNoise;

        if (randomSeed == 0) {
            this.randomGenerator = new Random();
        } else {
            this.randomGenerator = new Random(randomSeed);
        }

        //Throw away the first few because they first bits are not that random.
        randomGenerator.nextDouble();
        randomGenerator.nextDouble();
        reset();
    }

    public void addPuddle(Puddle newPuddle) {
        thePuddles.add(newPuddle);
    }

    public void clearPuddles() {
        thePuddles.clear();
    }

    /**
     * Returns an unmodifiable list of the puddles.
     *
     * @return
     */
    public List<Puddle> getPuddles() {
        return Collections.unmodifiableList(thePuddles);
    }

    public Point2D getPosition() {
        return agentPosition;
    }

    /**
     * Calculate the reward
     *
     * @return
     */
    public double getReward() {
        //System.out.println(agentPosition + ":Rewards:" + finalPuddleScore + "," + finalStepScore);
        double puddleReward = getPuddleReward();
        finalPuddleScore += puddleReward;

        if (inGoalRegion()) {
            finalStepScore += rewardAtGoal;
            return puddleReward + rewardAtGoal;
        } else {
            finalStepScore += rewardPerStep;
            return puddleReward + rewardPerStep;
        }
    }

    private double getPuddleReward() {
        double totalPuddleReward = 0;
        for (Puddle puddle : thePuddles) {
            totalPuddleReward += puddle.getReward(agentPosition);
        }
        return totalPuddleReward;
    }

    /**
     * IS the agent past the goal marker?
     *
     * @return
     */
    public boolean inGoalRegion() {
        return goalRect.contains(agentPosition);
    }

    protected void reset() {
        if (Parameters.parameters.booleanParameter("deterministic")) {
            this.randomGenerator = new Random(1);
        }
        if (CommonConstants.watch) {
            viewer.reset();
            viewer.drawGoal(goalRect);
            viewer.drawPuddles(thePuddles);
        }

        agentPosition.setLocation(defaultInitPosition);
        if (randomStarts) {
            do {
                double randStartX = .95d * randomGenerator.nextDouble();
                double randStartY = .95d * randomGenerator.nextDouble();
                agentPosition.setLocation(randStartX, randStartY);
            } while (inGoalRegion());
        }
        //System.out.println(" RESET! " + agentPosition);
    }

    void update(int a) {
        lastAction = a;

        double nextX = agentPosition.getX();
        double nextY = agentPosition.getY();

        if (a == 0) {
            nextX += agentSpeed;
        }
        if (a == 1) {
            nextX -= agentSpeed;
        }
        if (a == 2) {
            nextY += agentSpeed;
        }
        if (a == 3) {
            nextY -= agentSpeed;
        }

        double XNoise = randomGenerator.nextGaussian() * transitionNoise * agentSpeed;
        double YNoise = randomGenerator.nextGaussian() * transitionNoise * agentSpeed;

        nextX += XNoise;
        nextY += YNoise;

        nextX = Math.min(nextX, worldRect.getMaxX());
        nextX = Math.max(nextX, worldRect.getMinX());
        nextY = Math.min(nextY, worldRect.getMaxY());
        nextY = Math.max(nextY, worldRect.getMinY());

        agentPosition.setLocation(nextX, nextY);
        if (CommonConstants.watch) {
            viewer.visit(agentPosition);
        }
    }

    public int getLastAction() {
        return lastAction;
    }

    Observation makeObservation() {
        Observation currentObs = new Observation(0, 2);
        currentObs.doubleArray[0] = getPosition().getX();
        currentObs.doubleArray[1] = getPosition().getY();

        return currentObs;

    }
}
