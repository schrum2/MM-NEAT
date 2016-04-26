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
 * This is an agent controller nearly identical to the others in this package.
 * This class controls a fearful prey in a torus world
 * Fearful means the prey moves away from each predator in order of how close each predator is
 * 
 */
public class FearfulPreyController extends TorusPredPreyController {
    /** 
     * The getAction method takes in the controlled agent, the world and other predators and prey as arrays
     * The prey moves away from all the predators in a sequence depending on how close each predator is.
     */
    @Override
    public int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
        double[] moveDistances = new double[actions.length];
        for(int j = 0; j < preds.length; j++) {
            for(int i = 0; i < actions.length; i++) {
                double distance = preds[j].distance(me.getPosition().add(new Tuple2D(actions[i][0], actions[i][1])));
                moveDistances[i] += distance;
            }
        }
        return actions[StatisticsUtilities.argmax(moveDistances)];
    }

}
