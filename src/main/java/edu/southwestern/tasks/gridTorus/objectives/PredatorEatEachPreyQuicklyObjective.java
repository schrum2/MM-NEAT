package edu.southwestern.tasks.gridTorus.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;

/**
 * 
 * @author rollinsa reward the predators for each prey that gets eaten heavily
 *         encourages that the prey are eaten as quickly as possible
 */
public class PredatorEatEachPreyQuicklyObjective<T extends Network> extends GridTorusObjective<T> {

	@Override
	/**
	 * reward the predators for each prey that gets eaten heavily encourages
	 * that the prey are eaten as quickly as possible
	 */
	public double fitness(Organism<T> individual) {
		int numPrey = Parameters.parameters.integerParameter("torusPreys");
		double score = 0;
		// get the death time of each prey and subtract that from the score so
		// that quicker death times are encouraged
		for (int i = 0; i < numPrey; i++) {
			score -= game.getDeathTime(i);
		}
		return score;
	}

	@Override
	/**
	 * worst possible score for a predator is the full game time multiplied by
	 * how many prey there are (no prey eaten at all)
	 */
	public double minScore() {
		return -Parameters.parameters.integerParameter("torusTimeLimit")
				* Parameters.parameters.integerParameter("torusPreys");
	}
}
