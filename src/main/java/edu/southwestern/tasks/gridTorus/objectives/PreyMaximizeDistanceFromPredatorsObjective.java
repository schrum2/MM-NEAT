package edu.southwestern.tasks.gridTorus.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;

/**
 * 
 * @author rollinsa
 * 
 *         reward the prey for being as far from the predators as possible at
 *         the end of the game if the prey are dead then distance for that prey
 *         is counted as zero (meaning that the prey are also inherently
 *         encouraged to survive because it is trying to maximize distance)
 */
public class PreyMaximizeDistanceFromPredatorsObjective<T extends Network> extends GridTorusObjective<T> {

	@Override
	/**
	 * reward the prey for being as far from the predators as possible at the
	 * end if the prey are dead then distance for that prey is counted as zero
	 * (meaning that the prey are also inherently encouraged to survive because
	 * it is trying to maximize distance)
	 */
	public double fitness(Organism<T> individual) {
		// want this to be as high as possible to maximize distance from prey to
		// each pred
		return PredatorMinimizeDistanceFromPreyObjective.sumOfPredToPreyDistances(game.getPredators(), game.getPrey());
	}

}