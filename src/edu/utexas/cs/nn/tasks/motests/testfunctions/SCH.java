package edu.utexas.cs.nn.tasks.motests.testfunctions;

import edu.utexas.cs.nn.evolution.fitness.FitnessFunction;

/**
 *
 * @author Jacob Schrum
 */
public class SCH implements FunctionOptimizationSet {

    public double[] getLowerBounds() {
        return new double[]{-Math.pow(10, 3)};
    }

    public double[] getUpperBounds() {
        return new double[]{Math.pow(10, 3)};
    }

    public FitnessFunction[] getFitnessFunctions() {
        return new FitnessFunction[]{new SCHFunction(false), new SCHFunction(true)};
    }

    public double[] frontDecisionValuesInTermsOfFirst(double x1) {
        return new double[]{x1};
    }

    public double[] frontDecisionValuesBoundsOfFirst() {
        return new double[]{0, 2};
    }
}
