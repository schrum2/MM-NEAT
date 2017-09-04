package edu.southwestern.evolution.mulambda;

import edu.southwestern.tasks.SinglePopulationTask;

/**
 * The (mu+lambda) selection strategy. The next parent population comes from the
 * combined parent/child population, allowing strong parents to persist in the
 * population.
 * 
 * @author Jacob Schrum
 * @param <T>
 *            Type of phenotype evolved.
 */
public abstract class MuPlusLambda<T> extends MuLambda<T> {

	/**
	 * Creates a muLambda instance, based off of the 
	 * (mu+lambda) selection strategy. The next parent population comes from the
	 * combined parent/child population, allowing strong parents to persist in the population.
	 * @param task, an instance of a SinglePopulationTask
	 * @param mu, size of parent population as an integer
	 * @param lambda, size of child population as an integer
	 * @param io, true if logging is desired, false if not
	 */
	public MuPlusLambda(SinglePopulationTask<T> task, int mu, int lambda, boolean io) {
		super(MLTYPE_PLUS, task, mu, lambda, io);
	}
}
