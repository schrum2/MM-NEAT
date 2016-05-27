package edu.utexas.cs.nn.tasks.mspacman.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.networks.Network;

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
