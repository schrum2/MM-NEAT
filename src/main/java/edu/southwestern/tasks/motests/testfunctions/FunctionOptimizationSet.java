package edu.southwestern.tasks.motests.testfunctions;

import edu.southwestern.evolution.fitness.FitnessFunction;

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
