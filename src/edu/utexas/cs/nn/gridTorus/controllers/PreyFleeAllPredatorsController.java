
package edu.utexas.cs.nn.gridTorus.controllers;

import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import edu.utexas.cs.nn.util.util2D.Tuple2D;

/**
 *
 * @author Jacob Schrum
 * 
 *         This is an agent controller nearly identical to the others in this
 *         package. This class controls a fearful prey in a torus world. Fearful
 *         means the prey moves away from each predator in order of how close
 *         each predator is
 * 
 */
public class PreyFleeAllPredatorsController extends TorusPredPreyController {
	/**
	 * The getAction method takes in the controlled agent, the world and
	 * predators and prey as arrays. The prey moves away from all the predators
	 * in a sequence depending on how close each predator is.
	 */
	@Override
	public int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
		double[] moveDistances = new double[preyActions().length];
		for (int j = 0; j < preds.length; j++) {
			for (int i = 0; i < preyActions().length; i++) {
				double distance = preds[j]
						.distance(me.getPosition().add(new Tuple2D(preyActions()[i][0], preyActions()[i][1])));
				moveDistances[i] += distance;
			}
		}
		return preyActions()[StatisticsUtilities.argmax(moveDistances)];
	}

}
