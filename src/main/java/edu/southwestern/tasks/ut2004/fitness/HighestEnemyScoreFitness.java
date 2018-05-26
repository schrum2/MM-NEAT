package edu.southwestern.tasks.ut2004.fitness;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;

/**
 * The best enemy score, as a negative fitness
 *
 * @author Jacob Schrum
 */
public class HighestEnemyScoreFitness<T extends Network> extends UT2004FitnessFunction<T> {

	/**
	 * @return returns the highest enemy score as a negative value
	 */
	public double fitness(Organism<T> individual) {
		return -game.bestEnemyScore();
	}
}
