package edu.utexas.cs.nn.gridTorus.controllers;

import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.util.random.RandomNumbers;

/**
 *
 * @author Rollinsa
 * 
 * This is an agent controller nearly identical to the others in this class.
 * This class simply puts random number outputs into the predator's movements
 */
public class RandomPredatorController extends TorusPredPreyController {

	/**
	 * This method facilitates the use of a seeded random number generator to produce pseudorandom 
	 * movements in the predators
	 */
    @Override
    public int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
        return PREDATOR_ACTIONS[RandomNumbers.randomGenerator.nextInt(PREDATOR_ACTIONS.length)];
    }

}