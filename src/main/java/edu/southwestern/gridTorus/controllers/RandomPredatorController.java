package edu.southwestern.gridTorus.controllers;

import edu.southwestern.gridTorus.TorusAgent;
import edu.southwestern.gridTorus.TorusWorld;
import edu.southwestern.util.random.RandomNumbers;

/**
 *
 * @author Rollinsa
 * 
 *         This is an agent controller nearly identical to the others in this
 *         class. This class simply puts random number outputs into the
 *         predator's movements
 */
public class RandomPredatorController extends TorusPredPreyController {

	/**
	 * This method facilitates the use of a seeded random number generator to
	 * produce pseudorandom movements in the predators
	 */
	@Override
	public int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
		return predatorActions()[RandomNumbers.randomGenerator.nextInt(predatorActions().length)];
	}

}