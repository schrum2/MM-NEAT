package edu.southwestern.tasks.gridTorus.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.gridTorus.TorusAgent;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;

/**
 * 
 * @author rollinsa
 * 
 *         Encourages catching all prey with a very high score for doing so if
 *         the prey aren't all caught, this fitness function will emphasize that
 *         as many prey are caught as possible, and if not all are caught then
 *         it minimizes distance to the prey, and if some prey are caught but
 *         not all, quickness is encouraged (in addition to the distance still)
 */
public class PredatorCatchCloseQuickObjective<T extends Network> extends GridTorusObjective<T> {

	public static final double NO_PREY_SCORE = 10;

	@Override
	/**
	 * Find the score of the predator based on if all prey died or not
	 */
	public double fitness(Organism<T> individual) {

		TorusAgent[] prey = game.getPrey();
		TorusAgent[] preds = game.getPredators();
		int numPrey = prey.length;
		int numPreds = preds.length;
		double numCaught = 0;

		for (TorusAgent p : prey) {
			if (p == null) {
				numCaught++;
			}
		}
		// when all prey have been caught. Best possible score
		if (numCaught == numPrey)
			return NO_PREY_SCORE;

		double sumOfDistances = PredatorMinimizeDistanceFromPreyObjective.sumOfPredToPreyDistances(preds, prey);
		int height = Parameters.parameters.integerParameter("torusYDimensions");
		int width = Parameters.parameters.integerParameter("torusXDimensions");
		// max possible distance is the sum of half the world height and width
		// because the world wraps around
		double maxDistance = height / 2 + width / 2;
		// d = the normalized sum of distances from the predator to each prey at
		// the end of the simulation
		double d = sumOfDistances / (numPrey * numPreds * maxDistance);

		assert 0 <= d : "normalized distance less than 0! " + d;
		assert 1 >= d : "normalized distance greater than 1! " + d;

		// make d essentially its inverse so that less distance is encouraged
		d = 1 - d;

		// find the speed that the prey that were caught were caught (if not
		// caught, just returns total time for that prey)
		double sumOfDeathTimes = 0;
		for (int i = 0; i < numPrey; i++) {
			sumOfDeathTimes += game.getDeathTime(i);
		}
		double speed = sumOfDeathTimes / (numPrey * game.getTimeLimit());
		// make speed essentially its inverse so that less survival time is
		// encouraged
		speed = 1 - speed;

		// needs to be less than the maximum score, NO_PREY_SCORE, which is
		// given when all prey are caught
		double WEIGHT = (NO_PREY_SCORE / (numPrey + 1.0));

		// distance score is weighted to be less than catching each prey, but
		// the distance score still helps the predators
		// get close to the prey if they haven't learned to catch any prey yet
		// catching no prey will never be more points than catching one prey
		return ((d * (WEIGHT/2.0)) + (speed * (WEIGHT/2.0))) + (numCaught * WEIGHT);
	}

}