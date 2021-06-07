package edu.southwestern.evolution.mapelites.generalmappings;

import java.util.ArrayList;
import java.util.List;

import cern.colt.Arrays;
import edu.southwestern.evolution.mapelites.BinLabels;

/**
 * Basic abstract binning scheme with a range 
 * of zero to the provided max value, within
 * a specified amount of dimensions and amount
 * of bins per dimension.
 * 
 * @author Maxx Batterton
 *
 */
public abstract class MultiDimensionalRealValuedBinLabels implements BinLabels {

	List<String> labels = null;
	private int binsPerDimension; // number of bins in each dimension
	private double minPossibleValue; // min possible value, lowest value an input could have and still be valid
	private double maxPossibleValue; // max possible value, highest value an input could have and still be valid
	private double segmentSize; // difference between two adjacent bins, the size of the bin
	private int numDimensions; //  number of dimensions overall
	
	/**
	 * Sets up variables for use in other functions 
	 * in the binning scheme, to be specified in the
	 * constructor of a derivative binning scheme.
	 * 
	 * @param binsPerDimension Amount of bins in each dimension
	 * @param minPossibleValue Minimum value of bins, to bound the values to a certain point 
	 * @param maxPossibleValue Maximum value of bins, to bound the values to a certain point
	 * @param numDimensions Number of dimensions in the binning scheme
	 * @param vectorLength The length of the vector to be evolved
	 */
	public MultiDimensionalRealValuedBinLabels(int binsPerDimension, double minPossibleValue, double maxPossibleValue, int numDimensions, int vectorLength) {
		this.binsPerDimension = binsPerDimension;
		this.minPossibleValue = minPossibleValue*vectorLength; // should be negated max value or zero
		this.maxPossibleValue = maxPossibleValue*vectorLength;
		this.segmentSize = (double) (((maxPossibleValue-minPossibleValue)*vectorLength) / binsPerDimension); // size divided up
		this.numDimensions = numDimensions;
	}
	
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			int size = (int) Math.floor(Math.pow(binsPerDimension, numDimensions)); // get overall amount of bins
			labels = new ArrayList<String>(size);
			generateLabel(")", 0); // start recursive label generator
		}
		return labels;
	}
	
	
	/**
	 * Recursively create bin labels to account for 
	 * different dimensions, assembling them like
	 * coordinates; [x, y, z, ...]
	 * 
	 * @param input Current fragment of a label
	 * @param step Current coordinate to add
	 */
	private void generateLabel(String input, int step) {
		if (step == numDimensions) {
			labels.add("(" + input); // all dimensions added, cap the label
		} else {
			for (double i = 0; i < binsPerDimension; i++) {
				String newInput = input;
				if (step != 0) {
					newInput = ", " + newInput; 
				}
				newInput = ("[" + ((i*segmentSize)+minPossibleValue) + " to " + (((i+1)*segmentSize)+minPossibleValue) +"]") + newInput; // add dimension component to label
				generateLabel(newInput, step+1); // go to next dimension to add next part
			}
		}
		
	}
	
	
	@Override
	public int oneDimensionalIndex(int[] multi) {
		//System.out.println("Multi-dimensional array: "+Arrays.toString(multi));
		int index = 0;
		for (int i = 0; i < numDimensions; i++) {
			index += multi[i] * Math.pow(binsPerDimension, i); // get the 1D index of a bin
		}
		//System.out.println("One dimensional index: "+index);
		return index;
	}
	
	

	/**
	 * Discretizes given values into coordinates of where a bin is located.
	 * 
	 * @param behaviorCharacterization Two double values (x and y) to be discretized
	 * @return Coordinates of a bin given the coordinates
	 */
	public int[] discretize(double[] behaviorCharacterization) {
		int[] dbc = new int[numDimensions];
		for (int i = 0; i < numDimensions; i++) {
			if (behaviorCharacterization[i] > maxPossibleValue) throw new IllegalStateException(behaviorCharacterization[i]+ " exceeds maximum value specified ("+maxPossibleValue+")"); 
			if (behaviorCharacterization[i] < minPossibleValue) throw new IllegalStateException(behaviorCharacterization[i]+ " is below minimum value specified ("+minPossibleValue+")"); 
			double scaledValue = (behaviorCharacterization[i]-minPossibleValue) / (maxPossibleValue-minPossibleValue);
			dbc[i] = (int) Math.floor(scaledValue * binsPerDimension);
			if (dbc[i] == binsPerDimension) {
				dbc[i]--;
			}
		}
		//System.out.println("Discritizing \""+Arrays.toString(behaviorCharacterization)+"\" to bin \""+Arrays.toString(dbc)+"\"");
		return dbc;
	}
	
	
	// Behavior characterization depends on the specific binning scheme
	// public static double[] behaviorCharacterization(...);
}