package edu.southwestern.tasks.gridTorus.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;

/**
 * reward the prey for surviving as long as possible More points are given for
 * having later death times if the prey does die (largest score given per prey
 * automatically if it survives)
 * 
 * @author rollinsa
 */
public class PreyLongSurvivalTimeObjective<T extends Network> extends GridTorusObjective<T> {

	@Override
	/**
	 * reward the prey for surviving as long as possible More points are given
	 * for having later death times if the prey does die (largest score given
	 * per prey automatically if it survives)
	 */
	public double fitness(Organism<T> individual) {
		int numPrey = Parameters.parameters.integerParameter("torusPreys");
		double score = 0;
		// get the death time of each prey and add that from the score so that
		// later death times are encouraged
		for (int i = 0; i < numPrey; i++) {
			score += game.getDeathTime(i);
		}
		return score;
	}

}
