package edu.southwestern.tasks.ut2004.fitness;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;

/**
 * Measures how good the bot is at fragging (killing) other players 
 * @author Jacob Schrum
 */
public class FragFitness<T extends Network> extends UT2004FitnessFunction<T> {

	/**
	 * @return returns the number of times the bot fragged (killed) another player
	 */
	public double fitness(Organism<T> individual) {
		return game.getFrags(); //Frags = kills
	}
}
