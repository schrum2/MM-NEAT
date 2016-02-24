package edu.utexas.cs.nn.tasks.motests.testfunctions;

import edu.utexas.cs.nn.evolution.fitness.FitnessFunction;

/**
 *
 * @author Jacob Schrum
 */
public interface FunctionOptimizationSet {

    public double[] getLowerBounds();

    public double[] getUpperBounds();

    public FitnessFunction[] getFitnessFunctions();

    public double[] frontDecisionValuesBoundsOfFirst();

    public double[] frontDecisionValuesInTermsOfFirst(double x1);
}
