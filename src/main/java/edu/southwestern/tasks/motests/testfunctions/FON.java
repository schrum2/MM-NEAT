package edu.southwestern.tasks.motests.testfunctions;

import java.util.ArrayList;

import edu.southwestern.evolution.fitness.FitnessFunction;

/**
 *
 * @author Jacob Schrum
 */
public class FON implements FunctionOptimizationSet {

	public double[] getLowerBounds() {
		return new double[] { -4, -4, -4 };
	}

	public double[] getUpperBounds() {
		return new double[] { 4, 4, 4 };
	}

	@SuppressWarnings("unchecked")
	public FitnessFunction<ArrayList<Double>>[] getFitnessFunctions() {
		return new FitnessFunction[] { new FONFunction(1), new FONFunction(-1) };
	}

	public double[] frontDecisionValuesInTermsOfFirst(double x1) {
		return new double[] { x1, x1, x1 };
	}

	public double[] frontDecisionValuesBoundsOfFirst() {
		return new double[] { -1.0 / Math.sqrt(3.0), 1.0 / Math.sqrt(3.0) };
	}
}
