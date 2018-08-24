package edu.southwestern.tasks.ut2004.fitness;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;

/**
 * Measures how high the bot can score
 * @author Jacob Schrum
 */
public class ScoreFitness<T extends Network> extends UT2004FitnessFunction<T> {

	/**
	 * @return returns the bot's score for the game
	 */
	public double fitness(Organism<T> individual) {
		return game.getScore();
	}
}
