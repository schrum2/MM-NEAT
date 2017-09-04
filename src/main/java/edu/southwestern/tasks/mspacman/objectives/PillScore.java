package edu.southwestern.tasks.mspacman.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;

/**
 * Returns the number of pills eaten, including power pills. This creates a
 * slight scaling problem, since power pills are actually worth more in terms of
 * game score than regular pills.
 * 
 * @author Jacob Schrum
 */
public class PillScore<T extends Network> extends MsPacManObjective<T> {

	/**
	 * Number of eaten pills returns from GameFacade. Organism individual not
	 * needed.
	 */
	public double fitness(Organism<T> individual) {
		return g.getEatenPills();
	}
}
