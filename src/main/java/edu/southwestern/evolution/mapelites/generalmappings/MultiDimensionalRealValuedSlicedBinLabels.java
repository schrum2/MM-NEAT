package edu.southwestern.evolution.mapelites.generalmappings;

import java.util.Arrays;
import java.util.HashMap;

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
	
	private int binsPerDimension;
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
	 * @param minPossibleValue Minimum possible value
	 * @param maxPossibleValue Maximum possible value
	 * @param vectorLength Latent vector length
	 */
	public MultiDimensionalRealValuedSlicedBinLabels(int binsPerDimension, double minPossibleValue, double maxPossibleValue, int vectorLength) {
		super(binsPerDimension, minPossibleValue, maxPossibleValue, Parameters.parameters.integerParameter("solutionVectorSlices"), vectorLength/Parameters.parameters.integerParameter("solutionVectorSlices"));
		this.binsPerDimension = binsPerDimension;
		solutionVectorLength = vectorLength;
		solutionVectorSlices = Parameters.parameters.integerParameter("solutionVectorSlices");
		if (!(solutionVectorSlices > 1)) throw new IllegalStateException("MultiDimensionalRealValuedSlicedBinLabels must have more than 1 slice!");
	}
	
	public double[] behaviorCharacterization(double[] solution) {
		double[] sums = new double[solutionVectorSlices]; // create array for sums
		int sliceSize = solutionVectorLength/solutionVectorSlices;
		for (int i = 0; i < solutionVectorSlices; i++) {
			assert sums[i] == 0;
			for (int j = i*sliceSize; j < (i+1)*sliceSize; j++) {
				sums[i] += process(solution[j]); // sum each segment
			}
		}
		if (EXTRA_LOGGING) System.out.println("Characterized \""+Arrays.toString(solution)+"\" to \""+Arrays.toString(sums)+"\"");
		return sums;
	}
	
	/**
	 * Process the values if needed.
	 * 
	 * @param The value to be processed
	 * @return A processed value
	 */
	protected abstract double process(double value);
	
	@Override
	public String[] dimensions() {
		String[] dimensionNames = new String[solutionVectorSlices];
		for (int i = 1; i <= solutionVectorSlices; i++) {
			dimensionNames[i-1] = "Slice "+i;
		}
		return dimensionNames;
	}	
	
	@Override
	public int[] dimensionSizes() {
		int[] dimensionSizes = new int[solutionVectorSlices];
		for (int i = 1; i <= solutionVectorSlices; i++) {
			dimensionSizes[i-1] = binsPerDimension;
		}
		return dimensionSizes;
	}
	
	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		double[] latentVector = (double[]) keys.get("Solution Vector");
		return discretize(behaviorCharacterization(latentVector));
	}	
}
