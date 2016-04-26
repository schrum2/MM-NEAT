package edu.utexas.cs.nn.gridTorus.controllers;

import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.util.random.RandomNumbers;

/**
 *
 * @author Jacob Schrum
 * 
 * This is an agent controller nearly identical to the others in this class.
 * This class simply puts random number outputs into the predator and prey's movements
 */
public class RandomController extends TorusPredPreyController {

	/**
	 * This method facilitates the use of a seeded random number generator to produce pseudorandom 
	 * movements in both the predators and prey
	 */
    @Override
    public int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
        return actions[RandomNumbers.randomGenerator.nextInt(actions.length)];
    }

}
