package edu.southwestern.tasks.mspacman.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;

/**
 * Fitness based on standard Ms. Pac-Man game score
 * 
 * @author Jacob Schrum
 */
public class GameScore<T extends Network> extends MsPacManObjective<T> {

	/**
	 * Return game score earned by Ms. Pac-Man. The Organism individual is not
	 * needed.
	 */
	public double fitness(Organism<T> individual) {
		return g.getScore();
	}
}
