
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
 * This class controls a fearful prey in a torus world.
 * Fearful means the prey moves away from each predator in order of how close each predator is
 * 
 */
public class PreyFleeClosestPredatorController extends TorusPredPreyController {
	/** 
	 * The getAction method takes in the controlled agent, the world and predators and prey as arrays.
	 * The prey moves away from the closest predator in a sequence depending on what sequence from the
	 * prey's possible move sequences leaves them the farthest away from the closest predator.
	 */
	@Override
	public int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
		TorusAgent closestPredator = me.closestAgent(preds);
		double[] moveDistances = new double[PREY_ACTIONS.length];
		for(int i = 0; i < PREY_ACTIONS.length; i++) {
			double distance = closestPredator.distance(me.getPosition().add(new Tuple2D(PREY_ACTIONS[i][0], PREY_ACTIONS[i][1])));
			moveDistances[i] = distance;
		}
		return PREY_ACTIONS[StatisticsUtilities.argmax(moveDistances)];
	}

}