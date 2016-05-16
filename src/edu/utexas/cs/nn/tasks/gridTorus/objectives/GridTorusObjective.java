package edu.utexas.cs.nn.tasks.gridTorus.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.fitness.FitnessFunction;
import edu.utexas.cs.nn.gridTorus.TorusPredPreyGame;
import edu.utexas.cs.nn.networks.Network;

/**
 * parent class for all of the fitness score/objective classes for the gridTorus world
 * @author rollinsa
 *
 */
public abstract class GridTorusObjective<T extends Network> implements FitnessFunction<T> {

	/**
	 * the torus game
	 */
	protected TorusPredPreyGame game;

	/**
	 * 
	 * @param game TorusPredPrey grid world game
	 * @param individual organism to provide a fitness function for
	 * @return the score/fitness of the individual
	 */
	public double score(TorusPredPreyGame game, Organism<T> individual) {
		this.game = game;
		return fitness(individual);
	}


	/**
	 * Default minimum value for a score is 0, though this
	 * could be overridden.
	 * @return Default min score of 0
	 */
	public double minScore() {
		return 0;
	}

}
