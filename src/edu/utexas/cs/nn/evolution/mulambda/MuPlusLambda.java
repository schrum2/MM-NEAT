package edu.utexas.cs.nn.evolution.mulambda;

import edu.utexas.cs.nn.tasks.SinglePopulationTask;

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

	public MuPlusLambda(SinglePopulationTask<T> task, int mu, int lambda, boolean io) {
		super(MLTYPE_PLUS, task, mu, lambda, io);
	}
}
