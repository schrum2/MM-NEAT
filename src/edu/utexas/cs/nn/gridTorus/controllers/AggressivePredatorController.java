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
 */
public class AggressivePredatorController extends TorusPredPreyController {
    
    @Override
    public int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
        TorusAgent target = me.closestAgent(prey);
        double[] moveDistances = new double[actions.length];
        for(int i = 0; i < actions.length; i++) {
            moveDistances[i] = target.distance(me.getPosition().add(new Tuple2D(actions[i][0], actions[i][1])));
        }
        return actions[StatisticsUtilities.argmin(moveDistances)];
    }

}
