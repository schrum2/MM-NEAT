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
public class FearfulPreyController extends TorusPredPreyController {
    
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
