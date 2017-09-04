package edu.southwestern.tasks.gridTorus.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;

/**
 * 
 * @author rollinsa
 *
 *         maximize the total game time
 */
public class PreyMaximizeGameTimeObjective<T extends Network> extends GridTorusObjective<T> {

	@Override
	/**
	 * maximize the total game time
	 */
	public double fitness(Organism<T> individual) {
		return game.getTime();
	}

}