package edu.utexas.cs.nn.tasks.gridTorus.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.networks.Network;

/**
 * 
 * @author rollinsa
 * 
 *         Find the score of the predators based on how many prey died
 */
public class PredatorCatchObjective<T extends Network> extends GridTorusObjective<T> {

	@Override
	/**
	 * Find the score of the predators based on how many prey died
	 */
	public double fitness(Organism<T> individual) {

		TorusAgent[] prey = game.getPrey();
		double numCaught = 0;

		// get number of caught prey
		for (TorusAgent p : prey) {
			if (p == null) {
				numCaught++;
			}
		}

		// return a score based on number of caught prey
		return numCaught;
	}

}