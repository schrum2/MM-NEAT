package edu.utexas.cs.nn.tasks.gridTorus.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.networks.Network;

public class PreyMaximizeGameTime<T extends Network> extends GridTorusObjective<T> {

	@Override
	/**
	 * maximize the total game time
	 */
	public double fitness(Organism<T> individual) {
		return game.getTime();
	}

}