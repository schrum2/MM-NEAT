package edu.utexas.cs.nn.evolution.mulambda;

import edu.utexas.cs.nn.tasks.SinglePopulationTask;

/**
 *
 * @author Jacob Schrum
 */
public abstract class MuPlusLambda<T> extends MuLambda<T> {

    public MuPlusLambda(SinglePopulationTask<T> task, int mu, int lambda, boolean io) {
        super(MLTYPE_PLUS, task, mu, lambda, io);
    }
}
