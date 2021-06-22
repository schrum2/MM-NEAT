package edu.southwestern.tasks.innovationengines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;

/**
 * This class creates a binning scheme based on the number
 * of neurons and the number of links in a given image.
 * 
 * @author Anna Wicker
 *
 * @param <T>
 */
public class CPPNComplexityBinLabels implements BinLabels {
	
	List<String> labels = null;
	public static final int BIN_INDEX_NODES = 0;
	public static final int BIN_INDEX_LINKS = 1;
	
	public static final int MIN_NUM_NEURONS = 5;
	public static final int MIN_NUM_LINKS = 3;

	
	/**
	 * Creates the bin labels (coordinates corresponding
	 * to the correct bin).
	 * 
	 * @return List of bin labels as strings
	 */
	@Override
	public List<String> binLabels() {
		if(labels ==  null) {
			int size = (Parameters.parameters.integerParameter("maxNumNeurons") - MIN_NUM_NEURONS + 1)*(Parameters.parameters.integerParameter("maxNumLinks") - MIN_NUM_LINKS + 1);
			System.out.println("Archive Size: "+size);
			labels = new ArrayList<String>(size);
			int count = 0;
			for(int i = MIN_NUM_LINKS; i <= Parameters.parameters.integerParameter("maxNumLinks"); i++) {
				for(int j = MIN_NUM_NEURONS; j <= Parameters.parameters.integerParameter("maxNumNeurons"); j++) {
					labels.add("Neurons" + j + "links" + i);
					count++;
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
	 * @param multi An array containing two values: number of nodes, then number of links
	 * @return The index in multi containing the correct bin
	 */
	@Override
	public int oneDimensionalIndex(int[] multi) {
		int binIndex = ((multi[BIN_INDEX_NODES] - MIN_NUM_NEURONS) + (Parameters.parameters.integerParameter("maxNumNeurons") - MIN_NUM_NEURONS + 1) * (multi[BIN_INDEX_LINKS] - MIN_NUM_LINKS));
		assert binIndex >= 0 : "Negative index "+Arrays.toString(multi) + " -> " + binIndex;
		return binIndex;
	}
	
	@Override
	public String[] dimensions() {
		return new String[] {"Links", "Neurons"};
	}
	
	@Override
	public int[] dimensionSizes() {
		return new int[] {Parameters.parameters.integerParameter("maxNumLinks") - MIN_NUM_LINKS + 1, Parameters.parameters.integerParameter("maxNumNeurons") - MIN_NUM_NEURONS + 1};
	}
}
