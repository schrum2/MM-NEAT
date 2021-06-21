package edu.southwestern.tasks.innovationengines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;

public class CPPNNeuronScaleRotationDeltaXDeltaYBinLabels implements BinLabels{
	List<String> labels = null;
	
	public static final int BIN_INDEX_NODES = 0;
	public static final int MIN_NUM_NEURONS = 5;

	public CPPNNeuronScaleRotationDeltaXDeltaYBinLabels() {
		CPPNComplexityBinLabels.maxNumNeurons = Parameters.parameters.integerParameter("maxNumNeurons");
	}
	
	/**
	 * Creates the bin labels (coordinates corresponding
	 * to the correct bin).
	 * 
	 * @return List of bin labels as strings
	 */
	@Override
	public List<String> binLabels() {
		if(labels ==  null) {
			int size = (CPPNComplexityBinLabels.maxNumNeurons - MIN_NUM_NEURONS + 1);
			System.out.println("Archive Size: "+size);
			labels = new ArrayList<String>(size);
			int count = 0;
			for(int i = MIN_NUM_NEURONS; i <= CPPNComplexityBinLabels.maxNumNeurons; i++) {
				for(int j = 0; j < Parameters.parameters.doubleParameter("maxScale") / Parameters.parameters.integerParameter("scaleDivider"); j++) {
					for(int k = 0; k < 360; k++) {
						for(int m = 0; m < Parameters.parameters.doubleParameter("imageCenterTranslationRange"); m++) {
							for(int n = 0; n < Parameters.parameters.doubleParameter("imageCenterTranslationRange"); n++){
								labels.add("Neurons" + i + "-scale" + j + "-rotation" + k + "-deltaX" + m + "-deltaY" + n);
								count++;
							}
						}
					}
				}
			}
			assert count == size : "Incorrect number of bins created in archive: " + count;
		}
		return labels;
	}

	/**
	 * Calculating the index of the correct bin label
	 * in an int array multi.  
	 * 
	 * @param multi An array containing just one value, the number of neurons
	 * @return The index in multi containing the correct bin
	 */
	@Override
	public int oneDimensionalIndex(int[] multi) {
		int binIndex = ((multi[BIN_INDEX_NODES] - MIN_NUM_NEURONS));
		assert binIndex >= 0 : "Negative index "+Arrays.toString(multi) + " -> " + binIndex;
		return binIndex;
	}
	
	/**
	 * Gets the names of each bin dimension
	 */
	@Override
	public String[] dimensions() {
		return new String[] {"Neurons, Scale, Rotation, DeltaX, DeltaY"};
	}
	
	/**
	 * Gets the size of each archive dimension
	 */
	@Override
	public int[] dimensionSizes() {
		return new int[] {CPPNComplexityBinLabels.maxNumNeurons - MIN_NUM_NEURONS + 1};
	}
}
