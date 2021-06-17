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
	
	public static int maxNumNeurons;
	public static int maxNumLinks;
	public static final int MIN_NUM_NEURONS = 5;
	public static final int MIN_NUM_LINKS = 3;

	public CPPNComplexityBinLabels() {
		maxNumNeurons = Parameters.parameters.integerParameter("maxNumNeurons");
		maxNumLinks = Parameters.parameters.integerParameter("maxNumLinks");
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
			int size = (maxNumNeurons - MIN_NUM_NEURONS + 1)*(maxNumLinks - MIN_NUM_LINKS + 1);
			System.out.println("Archive Size: "+size);
			labels = new ArrayList<String>(size);
			int count = 0;
			for(int i = MIN_NUM_LINKS; i <= maxNumLinks; i++) {
				for(int j = MIN_NUM_NEURONS; j <= maxNumNeurons; j++) {
					labels.add("Neurons[" + j + "]links[" + i + "]");
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
		int binIndex = ((multi[BIN_INDEX_NODES] - MIN_NUM_NEURONS) + (maxNumNeurons - MIN_NUM_NEURONS + 1) * (multi[BIN_INDEX_LINKS] - MIN_NUM_LINKS));
		assert binIndex >= 0 : "Negative index "+Arrays.toString(multi) + " -> " + binIndex;
		return binIndex;
	}
	
	@Override
	public String[] dimensions() {
		return new String[] {"Links", "Neurons"};
	}
	
	@Override
	public int[] dimensionSizes() {
		return new int[] {maxNumLinks - MIN_NUM_LINKS + 1, maxNumNeurons - MIN_NUM_NEURONS + 1};
	}
}
