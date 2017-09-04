package edu.southwestern.tasks.mspacman.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;

/**
 *
 * @author Jacob Schrum
 */
public class FastGhostEatingScore<T extends Network> extends MsPacManObjective<T> {

	public double fitness(Organism<T> individual) {
		return g.getTimeGhostReward();
	}
}
