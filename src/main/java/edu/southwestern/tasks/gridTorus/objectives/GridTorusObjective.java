package edu.southwestern.tasks.gridTorus.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.evolution.fitness.FitnessFunction;
import edu.southwestern.gridTorus.TorusPredPreyGame;
import edu.southwestern.networks.Network;

/**
 * parent class for all of the fitness score/objective classes for the gridTorus
 * world
 * 
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
	 * @param game
	 *            TorusPredPrey grid world game
	 * @param individual
	 *            organism to provide a fitness function for
	 * @return the score/fitness of the individual
	 */
	public double score(TorusPredPreyGame game, Organism<T> individual) {
		this.game = game;
		return fitness(individual);
	}

	/**
	 * Default minimum value for a score is 0, though this could be overridden.
	 * 
	 * @return Default min score of 0
	 */
	public double minScore() {
		return 0;
	}

}
