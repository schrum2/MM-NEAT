package edu.utexas.cs.nn.tasks.ut2004.fitness;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.fitness.FitnessFunction;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.ut2004.bots.GameDataCollector;

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