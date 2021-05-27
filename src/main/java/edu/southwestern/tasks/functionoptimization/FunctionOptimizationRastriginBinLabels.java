package edu.southwestern.tasks.functionoptimization;

import java.util.List;

import edu.southwestern.evolution.mapelites.generalmappings.MultiDimensionalRealValuedBinLabels;

/**
 * Binning scheme for Rastrigin function, based on the description from
 * https://arxiv.org/pdf/1912.02400.pdf
 * 
 * @author Maxx Batterton
 *
 */
public class FunctionOptimizationRastriginBinLabels extends MultiDimensionalRealValuedBinLabels {

	List<String> labels = null;
	private int solutionVectorLength;
	private static final double RASTRIGIN_RANGE = 5.12;

	public FunctionOptimizationRastriginBinLabels() {
		super(500, -RASTRIGIN_RANGE/2, RASTRIGIN_RANGE/2, 2, 20);
		//Parameters.parameters.integerParameter("foBinDimension");
		solutionVectorLength = 20;//Parameters.parameters.integerParameter("foVectorLength");
	}


	/**
	 * Restricts the contribution of a value to within the typical rastrigin range.
	 * If within the range, just return, otherwise divide the range by the value and
	 * return.
	 * 
	 * @param x A value to be clipped
	 * @return The clipped value
	 */
	public static double clip(double x) {
		if (x >= -RASTRIGIN_RANGE && x <= RASTRIGIN_RANGE) {
			return x; // if already in range just return
		} else {
			return RASTRIGIN_RANGE / x; // otherwise divide range by x and return
		}
//		if (x > RASTRIGIN_RANGE) {			// This is a normal clamp, however since
//			return RASTRIGIN_RANGE;			// the reference paper:
//		} else if (x < -RASTRIGIN_RANGE) {	// (https://arxiv.org/pdf/1912.02400.pdf) 
//			return -RASTRIGIN_RANGE;		// did it the other way, that's how we're
//		} else {							// going to do it too.
//			return x;
//		}
	}

	/**
	 * Gets the behavior characterization of a solution using the rastrigin
	 * characterization described in https://arxiv.org/pdf/1912.02400.pdf
	 * 
	 * @param solution
	 * @return
	 */
	public double[] behaviorCharacterization(double[] solution) {
		double[] sums = new double[] { 0, 0 }; // create array for sums
		for (int i = 0; i < solutionVectorLength / 2; i++) {
			sums[0] += clip(solution[i]); // sum first half
		}
		for (int i = solutionVectorLength / 2; i < solutionVectorLength; i++) {
			sums[1] += clip(solution[i]); // sum second half
		}
		return sums;
	}
}
