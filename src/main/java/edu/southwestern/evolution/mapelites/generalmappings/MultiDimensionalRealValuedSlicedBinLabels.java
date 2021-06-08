package edu.southwestern.evolution.mapelites.generalmappings;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;

/**
 * Abstract binning scheme for anything
 * that needs to be sliced and summed. 
 * Default amount of slices is 2.
 * 
 * @author Maxx Batterton
 *
 */
public abstract class MultiDimensionalRealValuedSlicedBinLabels extends MultiDimensionalRealValuedBinLabels {
	
	private int solutionVectorLength;
	private int solutionVectorSlices;
	
	public MultiDimensionalRealValuedSlicedBinLabels(int binsPerDimension, double minPossibleValue, double maxPossibleValue, int vectorLength) {
		super(binsPerDimension, minPossibleValue, maxPossibleValue, Parameters.parameters.integerParameter("solutionVectorSlices"), vectorLength);
		solutionVectorLength = vectorLength;
		solutionVectorSlices = Parameters.parameters.integerParameter("solutionVectorSlices");
		if (!(solutionVectorSlices > 1)) {throw new IllegalStateException("MultiDimensionalRealValuedSlicedBinLabels must have more than 1 slice!");}
	}
	
	public double[] behaviorCharacterization(double[] solution) {
		double[] sums = new double[solutionVectorSlices]; // create array for sums
		int sliceRange = solutionVectorLength / solutionVectorSlices;
		for (int i = 0; i < solutionVectorSlices; i++) {
			for (int j = i*sliceRange; j < (i+1)*sliceRange; j++) {
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
