package edu.southwestern.tasks.innovationengines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;

public class CPPNNeuronCountBinLabels implements BinLabels{
	List<String> labels = null;
	public static final int BIN_INDEX_NODES = 0;
	
	public static final int MIN_NUM_NEURONS = 5;
	
	/**
	 * Creates the bin labels (coordinates corresponding
	 * to the correct bin).
	 * 
	 * @return List of bin labels as strings
	 */
	@Override
	public List<String> binLabels() {
		if(labels ==  null) {
			int size = (Parameters.parameters.integerParameter("maxNumNeurons") - MIN_NUM_NEURONS + 1);
			System.out.println("Archive Size: "+size);
			labels = new ArrayList<String>(size);
			int count = 0;
			for(int j = MIN_NUM_NEURONS; j <= Parameters.parameters.integerParameter("maxNumNeurons"); j++) {
				labels.add("Neurons" + j);
				count++;

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
	
	@Override
	public String[] dimensions() {
		return new String[] {"Neurons"};
	}
	
	@Override
	public int[] dimensionSizes() {
		return new int[] {Parameters.parameters.integerParameter("maxNumNeurons") - MIN_NUM_NEURONS + 1};
	}
}
