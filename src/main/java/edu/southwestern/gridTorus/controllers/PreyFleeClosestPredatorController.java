
package edu.southwestern.gridTorus.controllers;

import edu.southwestern.gridTorus.TorusAgent;
import edu.southwestern.gridTorus.TorusWorld;
import edu.southwestern.util.stats.StatisticsUtilities;
import edu.southwestern.util.util2D.Tuple2D;

/**
 *
 * @author Jacob Schrum
 * 
 *         This is an agent controller nearly identical to the others in this
 *         package. This class controls a fearful prey in a torus world. Fearful
 *         means the prey moves away from the closest predator
 * 
 */
public class PreyFleeClosestPredatorController extends TorusPredPreyController {

	public PreyFleeClosestPredatorController() {
		super();
	}

	/**
	 * The getAction method takes in the controlled agent, the world and
	 * predators and prey as arrays. The prey moves away from the closest
	 * predator in a sequence depending on what sequence from the prey's
	 * possible move sequences leaves them the farthest away from the closest
	 * predator.
	 */
	@Override
	public int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
		TorusAgent closestPredator = me.closestAgent(preds);
		double[] moveDistances = new double[preyActions().length];
		for (int i = 0; i < preyActions().length; i++) {
			double distance = closestPredator
					.distance(me.getPosition().add(new Tuple2D(preyActions()[i][0], preyActions()[i][1])));
			moveDistances[i] = distance;
		}
		return preyActions()[StatisticsUtilities.argmax(moveDistances)];
	}

}