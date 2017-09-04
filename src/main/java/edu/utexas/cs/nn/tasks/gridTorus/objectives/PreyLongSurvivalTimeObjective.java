package edu.utexas.cs.nn.tasks.gridTorus.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;

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
