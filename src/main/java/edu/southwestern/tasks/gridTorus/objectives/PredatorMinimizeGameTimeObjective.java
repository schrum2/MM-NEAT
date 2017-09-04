package edu.southwestern.tasks.gridTorus.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;

/**
 * 
 * @author rollinsa
 *
 *         minimize the total game time
 */
public class PredatorMinimizeGameTimeObjective<T extends Network> extends GridTorusObjective<T> {

	@Override
	/**
	 * minimize the total game time
	 */
	public double fitness(Organism<T> individual) {
		return -game.getTime();
	}

	@Override
	/**
	 * worst possible score for a predator is the full game time
	 */
	public double minScore() {
		return -Parameters.parameters.integerParameter("torusTimeLimit");
	}

}
