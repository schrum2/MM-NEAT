package edu.southwestern.tasks.ut2004.fitness;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;

/**
 * Measures how good the bot is at not dying
 * @author Jacob Schrum
 */
public class DeathsFitness<T extends Network> extends UT2004FitnessFunction<T> {

	/**
	 * @return returns the number of deaths as a negative value
	 */
	public double fitness(Organism<T> individual) {
		return -game.getDeaths();
	}
}
