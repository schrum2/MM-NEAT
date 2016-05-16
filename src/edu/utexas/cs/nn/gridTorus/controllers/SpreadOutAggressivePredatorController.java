package edu.utexas.cs.nn.gridTorus.controllers;

import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import edu.utexas.cs.nn.util.util2D.Tuple2D;

/**
*
* @author Rollinsa
 * 
 * This is an agent controller nearly identical to the others in this package.
 * This class controls an aggressive predator in a torus world. 
 * Aggressive means the TorusAgent locates the closest prey and
 * 'attacks' or moves towards said prey. Also emphasizes predator distance from one another
 */
public class SpreadOutAggressivePredatorController extends TorusPredPreyController {
    /** 
     * The getAction method takes in the controlled agent, the world and predators and prey as arrays.
     * The predator attempts to close the distance between itself and the closest prey
     * Predators also attempt to maintain distance from one another
     */
    @Override
    public int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
        TorusAgent target = me.closestAgent(prey);
        double[] moveDistances = new double[predatorActions().length];
        //Prefer the movement that gets me (predator) closest to prey
        for(int i = 0; i < predatorActions().length; i++) {
            moveDistances[i] = target.distance(me.getPosition().add(new Tuple2D(predatorActions()[i][0], predatorActions()[i][1])));
        }
        //prefer movement that gets me (predator) as far as possible from other predators
        for(int j = 0; j < preds.length; j++) {
            for(int i = 0; i < predatorActions().length; i++) {
                double distance = preds[j].distance(me.getPosition().add(new Tuple2D(predatorActions()[i][0], predatorActions()[i][1])));
                //Emphasizes that predators also stay away from one another. Weighted to be less than chasing prey
                //maybe change weighting to be (1/(preds.length * 2)) * distance
                moveDistances[i] -= (.5) * distance;
            }
        }
        return predatorActions()[StatisticsUtilities.argmin(moveDistances)];
    }

}
