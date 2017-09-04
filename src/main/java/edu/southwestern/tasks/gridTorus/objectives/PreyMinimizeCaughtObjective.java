package edu.utexas.cs.nn.tasks.gridTorus.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;

/**
 * 
 * @author rollinsa
 * 
 *         Find the score of the prey based on how many prey died
 */
public class PreyMinimizeCaughtObjective<T extends Network> extends GridTorusObjective<T> {

	@Override
	/**
	 * Find the score of the prey based on how many prey died
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

		// return a score based on percentage of caught prey (max score of 0)
		return -numCaught;
	}

	@Override
	public double minScore() {
		// min score is number of prey (so every prey was caught)
		return -Parameters.parameters.integerParameter("torusPreys");
	}

}
