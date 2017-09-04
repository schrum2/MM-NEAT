package edu.utexas.cs.nn.tasks.gridTorus.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;

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
