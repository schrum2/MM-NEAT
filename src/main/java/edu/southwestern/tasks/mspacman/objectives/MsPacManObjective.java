package edu.southwestern.tasks.mspacman.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.evolution.fitness.FitnessFunction;
import edu.southwestern.networks.Network;
import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 * Way of extracting a single score/objective value associated with Ms. Pac-Man
 *
 * @author Jacob Schrum
 * @param <T>
 *            Type of phenotype evolved (must be a neural network)
 */
public abstract class MsPacManObjective<T extends Network> implements FitnessFunction<T> {

	/**
	 * Always needed to calculate Ms. Pac-Man information since this is where
	 * all the data is stored
	 */
	protected GameFacade g;

	/**
	 * Provide the game facade and organism in order to calculate the score.
	 * Organism is needed by the FitnessFunction class, but for Ms. Pac-Man, the
	 * important information is actually stored in the GameFacade, which will be
	 * the actual source of score information in most fitness functions.
	 *
	 * @param g
	 *            GameFacade for Ms. Pac-Man: Contains all needed Ms. Pac-Man
	 *            information
	 * @param o
	 *            Organism provided for scores based the organism.
	 * @return numeric score/fitness value
	 */
	public double score(GameFacade g, Organism<T> o) {
		this.g = g;
		return fitness(o);
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
