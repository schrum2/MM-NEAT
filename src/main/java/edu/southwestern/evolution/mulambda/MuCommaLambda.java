package edu.southwestern.evolution.mulambda;

import edu.southwestern.tasks.SinglePopulationTask;

/**
 * The (mu,lambda) selection strategy. Parents generate children, and the next
 * parent population comes exclusively from those children.
 * 
 * @author Jacob Schrum
 * @param <T>
 *            Type of phenotype evolved
 */
public abstract class MuCommaLambda<T> extends MuLambda<T> {

	public MuCommaLambda(SinglePopulationTask<T> task, int mu, int lambda, boolean io) {
		super(MLTYPE_COMMA, task, mu, lambda, io);
	}
}
