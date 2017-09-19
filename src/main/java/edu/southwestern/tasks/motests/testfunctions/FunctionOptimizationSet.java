package edu.southwestern.tasks.motests.testfunctions;

import java.util.ArrayList;

import edu.southwestern.evolution.fitness.FitnessFunction;

/**
 * For optimizing groups of objective functions, such as though
 * originally used to benchmark NSGA-II
 * @author Jacob Schrum
 */
public interface FunctionOptimizationSet {

	public double[] getLowerBounds();

	public double[] getUpperBounds();

	/**
	 * Only supports evolution of real-valued genotypes
	 * @return
	 */
	public FitnessFunction<ArrayList<Double>>[] getFitnessFunctions();

	public double[] frontDecisionValuesBoundsOfFirst();

	public double[] frontDecisionValuesInTermsOfFirst(double x1);
}
