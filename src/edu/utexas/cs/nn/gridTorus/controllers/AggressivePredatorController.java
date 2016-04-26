/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utexas.cs.nn.gridTorus.controllers;

import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import edu.utexas.cs.nn.util.util2D.Tuple2D;

/**
 *
 * @author Jacob Schrum
 * 
 *This is an agent controller nearly identical to the others in this class.
 * This class controls an aggressive predator in a torus world. 
 * Aggressive means the TorusAgent locates the closest prey and
 * 'attacks' or moves towards said prey.
 */
public class AggressivePredatorController extends TorusPredPreyController {
 
    @Override
    /**
     * The getAction method takes in the controlled agent, the world and other predators and prey as arrays
     * The closest prey is chosen and the TorusAgent moves towards this prey for as long as the action sequence is.
     */
    public int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
        TorusAgent target = me.closestAgent(prey);
        double[] moveDistances = new double[actions.length];
        for(int i = 0; i < actions.length; i++) {
            moveDistances[i] = target.distance(me.getPosition().add(new Tuple2D(actions[i][0], actions[i][1])));
        }
        return actions[StatisticsUtilities.argmin(moveDistances)];
    }

}
