package edu.southwestern.tasks.ut2004.fitness;

import edu.southwestern.evolution.Organism;
import edu.southwestern.evolution.fitness.FitnessFunction;
import edu.southwestern.networks.Network;
import edu.southwestern.tasks.ut2004.bots.GameDataCollector;

/**
 *
 * @author Jacob Schrum
 */
public abstract class UT2004FitnessFunction<T extends Network> implements FitnessFunction<T> {

	GameDataCollector game;

	public double score(GameDataCollector g, Organism<T> o) {
		this.game = g;
		return fitness(o);
	}

	public double minScore() {
		return 0;
	}
}