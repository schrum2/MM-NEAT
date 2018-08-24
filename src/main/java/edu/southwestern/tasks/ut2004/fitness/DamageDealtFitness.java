package edu.southwestern.tasks.ut2004.fitness;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;

/**
 * Measusres how well the bot damages other players (most fit = caused the most damage)
 * @author Jacob Schrum
 */
public class DamageDealtFitness<T extends Network> extends UT2004FitnessFunction<T> {

	/**
	 * @return returns the total damage done by the bot
	 */
	public double fitness(Organism<T> individual) {
		return game.getDamageCaused();
	}
}
