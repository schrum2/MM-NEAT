package edu.southwestern.tasks.ut2004.fitness;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;

/**
 *Measures how good the bot is at getting kill streaks
 * @author Jacob Schrum
 */
public class StreakFitness<T extends Network> extends UT2004FitnessFunction<T> {

	/**
	 * @return returns the bot's longest streak
	 */
	public double fitness(Organism<T> individual) {
		return game.getStreak();
	}
}
