package edu.utexas.cs.nn.evolution.mulambda;

import edu.utexas.cs.nn.tasks.SinglePopulationTask;

/**
 *
 * @author Jacob Schrum
 */
public abstract class MuCommaLambda<T> extends MuLambda<T> {

    public MuCommaLambda(SinglePopulationTask<T> task, int mu, int lambda, boolean io) {
        super(MLTYPE_COMMA, task, mu, lambda, io);
    }
}
