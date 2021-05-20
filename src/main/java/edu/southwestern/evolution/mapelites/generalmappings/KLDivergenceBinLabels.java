package edu.southwestern.evolution.mapelites.generalmappings;

import java.util.ArrayList;
import java.util.List;

import cern.colt.Arrays;
import distance.convolution.ConvNTuple;
import distance.kl.KLDiv;
import distance.test.KLDivTest;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;

public class KLDivergenceBinLabels implements BinLabels {
	
	List<String> labels = null;
	private int binsPerDimension; // number of bins in a dimension
	private int maxPossibleValue; // max possible value of a bin
	private double segmentSize; // difference between two adjacent coordinates
	private static final int AMOUNT_OF_DIMENSIONS = 2;
	
	
	
	public KLDivergenceBinLabels() {
		binsPerDimension = 12; //Parameters.parameters.integerParameter("klDivBinDimension");
		maxPossibleValue = 60; //Parameters.parameters.integerParameter("klDivMaxValue");
		segmentSize = (double) maxPossibleValue / (double) binsPerDimension;
	}
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			
			int size = (int) Math.floor(Math.pow(binsPerDimension, AMOUNT_OF_DIMENSIONS));
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
		if (step == AMOUNT_OF_DIMENSIONS) {
			labels.add("[" + input);
		} else {
			for (int i = 0; i < binsPerDimension; i++) {
				String newInput = input;
				if (step != 0) {
					newInput = ", " + newInput;
				}
				newInput = ("(" + i*segmentSize + " to " + (i+1)*segmentSize +")") + newInput;
				generateLabel(newInput, step+1);
			}
		}
		
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		int index = 0;
		for (int i = 0; i < AMOUNT_OF_DIMENSIONS; i++) {
			index += multi[i] * Math.pow(binsPerDimension, i);
		}
		return index;
	}
	
	/**
	 * Gets the behavior characterization of a solution 
	 * 
	 * @param solution
	 * @return behavior characterization
	 */
	public double[] behaviorCharacterization(double[] solution) {
		return null;
	}

	/**
	 * Discretizes given values into coordinates of where a bin is located.
	 * 
	 * @param behaviorCharacterization Two double values (x and y) to be discretized
	 * @return Coordinates of a bin given the coordinates
	 */
	public int[] discretize(double[] behaviorCharacterization) {
		int[] dbc = new int[AMOUNT_OF_DIMENSIONS];
		for (int i = 0; i < AMOUNT_OF_DIMENSIONS; i++) {
			dbc[i] = (int) Math.floor(behaviorCharacterization[i] / segmentSize);
		}
		return dbc;
	}
	
	/**
	 * Calculate the KL Divergence of two provided levels
	 * 
	 * @param level1 The first level to compare
	 * @param level2 The second level to compare
	 * @return A double representing the KL divergence
	 */
	public static double getKLDivergence(int[][] level1, int[][] level2) {
		ConvNTuple c1 = KLDivTest.getConvNTuple(level1, Parameters.parameters.integerParameter("receptiveFieldWidth"), Parameters.parameters.integerParameter("receptiveFieldHeight"), Parameters.parameters.integerParameter("stride"));
		ConvNTuple c2 = KLDivTest.getConvNTuple(level2, Parameters.parameters.integerParameter("receptiveFieldWidth"), Parameters.parameters.integerParameter("receptiveFieldHeight"), Parameters.parameters.integerParameter("stride"));

		double klDiv = KLDiv.klDiv(c1.sampleDis, c2.sampleDis);
		
		return klDiv;
	}
	
	// test something here
	public static void main(String[] args) {
		KLDivergenceBinLabels test = new KLDivergenceBinLabels();
		List<String> labeles = test.binLabels();
		System.out.println(labeles);
		int[] dcoord = test.discretize(new double[] {43.0, 12.0});
		System.out.println(Arrays.toString(dcoord));
		System.out.println(labeles.get(test.oneDimensionalIndex(dcoord)));
		System.out.println();
	}
	
}
