package edu.southwestern.evolution.mapelites.generalmappings;

import edu.southwestern.parameters.Parameters;

/**
 * Abstract binning scheme that divides 
 * up a latent vector into a specfied 
 * number of segments, summing them,
 * and using that as the behavior
 * characteristic.
 * 
 * @author Maxx Batterton
 *
 */
public abstract class MultiDimensionalRealValuedSlicedBinLabels extends MultiDimensionalRealValuedBinLabels {
	
	private int solutionVectorLength;
	private int solutionVectorSlices;
	
	/**
	 * Constructor. Minimum possible value 
	 * and max possible should be 
	 * pre-divided based on the number of
	 * dimensions, and so should the vector 
	 * length.
	 * 
	 * @param binsPerDimension Number of bins in each dimension
	 * @param minPossibleValue Minimum possible value, scaled by the number of dimensions
	 * @param maxPossibleValue Maximum possible value, scaled by the number of dimensions
	 * @param vectorLength Latent vector length, scaled by the number of dimensions
	 */
	public MultiDimensionalRealValuedSlicedBinLabels(int binsPerDimension, double minPossibleValue, double maxPossibleValue, int vectorLength) {
		super(binsPerDimension, minPossibleValue, maxPossibleValue, Parameters.parameters.integerParameter("solutionVectorSlices"), vectorLength);
		solutionVectorLength = vectorLength;
		solutionVectorSlices = Parameters.parameters.integerParameter("solutionVectorSlices");
		if (!(solutionVectorSlices > 1)) throw new IllegalStateException("MultiDimensionalRealValuedSlicedBinLabels must have more than 1 slice!");
	}
	
	public double[] behaviorCharacterization(double[] solution) {
		double[] sums = new double[solutionVectorSlices]; // create array for sums
		for (int i = 0; i < solutionVectorSlices; i++) {
			assert sums[i] == 0;
			for (int j = i*solutionVectorLength; j < (i+1)*solutionVectorLength; j++) {
				sums[i] += process(solution[j]); // sum each segment
			}
		}
		return sums;
	}
	
	/**
	 * Process the values if needed.
	 * 
	 * @param The value to be processed
	 * @return A processed value
	 */
	protected abstract double process(double value);
	
}
