package edu.southwestern.tasks.ut2004.fitness;

import edu.southwestern.evolution.Organism;
import edu.southwestern.evolution.fitness.FitnessFunction;
import edu.southwestern.networks.Network;
import edu.southwestern.tasks.ut2004.bots.GameDataCollector;

/**
 * finds the fitness of organisms in a game
 * @author Jacob Schrum
 */
public abstract class UT2004FitnessFunction<T extends Network> implements FitnessFunction<T> {

	GameDataCollector game;

	/**
	 * 
	 * @param g (game in which to find the fitness of a given organism)
	 * @param o (organism to find the fitness of)
	 * @return returns the fitness of the given organism in the given game
	 */
	public double score(GameDataCollector g, Organism<T> o) {
		this.game = g;
		return fitness(o);
	}
	
	/**
	 * @return sets the minimum score to zero
	 */
	public double minScore() {
		return 0;
	}
}