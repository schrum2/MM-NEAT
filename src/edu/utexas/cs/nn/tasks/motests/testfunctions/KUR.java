package edu.utexas.cs.nn.tasks.motests.testfunctions;

import edu.utexas.cs.nn.evolution.fitness.FitnessFunction;

/**
 *
 * @author Jacob Schrum
 */
public class KUR implements FunctionOptimizationSet {

    public double[] getLowerBounds() {
        return new double[]{-5, -5, -5};
    }

    public double[] getUpperBounds() {
        return new double[]{5, 5, 5};
    }

    public FitnessFunction[] getFitnessFunctions() {
        return new FitnessFunction[]{new KURFunction(false), new KURFunction(true)};
    }

    public double[] frontDecisionValuesInTermsOfFirst(double x1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double[] frontDecisionValuesBoundsOfFirst() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
