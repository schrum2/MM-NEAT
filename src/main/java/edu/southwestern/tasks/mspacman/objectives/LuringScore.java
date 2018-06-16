package edu.southwestern.tasks.mspacman.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;

/**
 * Fitness score meant to reward Ms. Pac-Man for luring ghosts to the power pills before eating them.
 *
 * @author Jacob Schrum
 */
public class LuringScore<T extends Network> extends MsPacManObjective<T> {

	public double fitness(Organism<T> individual) {
		return g.getLureDistanceSum();
	}
}
