package edu.southwestern.tasks.ut2004.fitness;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;

/**
 * Measures how well a bot avoids being damaged by enemies
 * @author Jacob Schrum
 */
public class DamageReceivedFitness<T extends Network> extends UT2004FitnessFunction<T> {
	
	/**
	 * @return returns total damage taken as a negative number
	 */
	public double fitness(Organism<T> individual) {
		return -game.getDamageSuffered();
	}
}
