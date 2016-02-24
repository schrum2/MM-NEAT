package edu.utexas.cs.nn.tasks.motests.testfunctions;

import edu.utexas.cs.nn.evolution.fitness.FitnessFunction;

/**
 *
 * @author Jacob Schrum
 */
public class POL implements FunctionOptimizationSet {

    public double[] getLowerBounds() {
        return new double[]{-Math.PI, -Math.PI};
    }

    public double[] getUpperBounds() {
        return new double[]{Math.PI, Math.PI};
    }

    public FitnessFunction[] getFitnessFunctions() {
        return new FitnessFunction[]{new POLFunction(false), new POLFunction(true)};
    }

    public double[] frontDecisionValuesInTermsOfFirst(double x1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double[] frontDecisionValuesBoundsOfFirst() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
