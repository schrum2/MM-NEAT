package edu.southwestern.evolution.mapelites.generalmappings;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;

public abstract class MultiDimensionalRealValuedBinLabels implements BinLabels {

	List<String> labels = null;
	private int binsPerDimension; // number of bins in a dimension
	private double maxPossibleValue; // max possible value of a bin
	private double segmentSize; // difference between two adjacent coordinates
	private int numDimensions; // number of dimensions
	
	
	public MultiDimensionalRealValuedBinLabels(int binsPerDimension, double maxPossibleValue, int numDimensions) {
		this.binsPerDimension = binsPerDimension;
		this.maxPossibleValue = maxPossibleValue;
		this.segmentSize = (double) maxPossibleValue / binsPerDimension;  
		this.numDimensions = numDimensions;
	}
	
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			int size = (int) Math.floor(Math.pow(binsPerDimension, numDimensions));
			labels = new ArrayList<String>(size);
			generateLabel("]", 0); // start recursive label generator
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
			labels.add("[" + input);
		} else {
			for (int i = 0; i < binsPerDimension; i++) {
				String newInput = input;
				if (step != 0) {
					newInput = ", " + newInput;
				}
				newInput = ("[" + i*segmentSize + " to " + (i+1)*segmentSize +"]") + newInput;
				generateLabel(newInput, step+1);
			}
		}
		
	}
	
	
	@Override
	public int oneDimensionalIndex(int[] multi) {
		int index = 0;
		for (int i = 0; i < numDimensions; i++) {
			index += multi[i] * Math.pow(binsPerDimension, i);
		}
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
			if (behaviorCharacterization[i] > maxPossibleValue) throw new IllegalStateException(behaviorCharacterization[i]+ " exceeds KL Divergence maximum value specified ("+maxPossibleValue+")"); 
			dbc[i] = (int) Math.floor(behaviorCharacterization[i] / segmentSize);
		}
		//System.out.println("Discritizing \""+Arrays.toString(behaviorCharacterization)+"\" to bin \""+Arrays.toString(dbc)+"\"");
		return dbc;
	}
	
	
}
