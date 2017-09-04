package edu.southwestern.gridTorus.controllers;

import edu.southwestern.gridTorus.TorusAgent;
import edu.southwestern.gridTorus.TorusWorld;
import edu.southwestern.util.random.RandomNumbers;

/**
 *
 * @author Jacob Schrum
 * 
 *         This is an agent controller nearly identical to the others in this
 *         class. This class simply puts random number outputs into the prey's
 *         movements
 */
public class RandomPreyController extends TorusPredPreyController {

	/**
	 * This method facilitates the use of a seeded random number generator to
	 * produce pseudorandom movements in the prey
	 */
	@Override
	public int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
		return preyActions()[RandomNumbers.randomGenerator.nextInt(preyActions().length)];
	}

}
