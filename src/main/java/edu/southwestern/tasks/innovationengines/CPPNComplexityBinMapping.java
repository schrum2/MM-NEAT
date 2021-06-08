package edu.southwestern.tasks.innovationengines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;

public class CPPNComplexityBinMapping<T extends Network> implements BinLabels {
	
	List<String> labels = null;
	public static final int BIN_INDEX_NODES = 0;
	public static final int BIN_INDEX_LINKS = 1;
	
	public static int MAX_NUM_NEURONS;
	public static int MAX_NUM_LINKS;
	public static final int MIN_NUM_NEURONS = 5;
	public static final int MIN_NUM_LINKS = 3;

	public CPPNComplexityBinMapping() {
		MAX_NUM_NEURONS = Parameters.parameters.integerParameter("maxNumNeurons");
		MAX_NUM_LINKS = Parameters.parameters.integerParameter("maxNumLinks");
	}
	
	@Override
	public List<String> binLabels() {
		if(labels ==  null) {
			int size = (MAX_NUM_NEURONS - MIN_NUM_NEURONS + 1)*(MAX_NUM_LINKS - MIN_NUM_LINKS + 1);
			System.out.println("Archive Size: "+size);
			labels = new ArrayList<String>(size);
			int count = 0;
			for(int i = MIN_NUM_LINKS; i <= MAX_NUM_LINKS; i++) {
				for(int j = MIN_NUM_NEURONS; j <= MAX_NUM_NEURONS; j++) {
					labels.add("Neurons[" + j + "]links[" + i + "]");
					count++;
				}
			}
			assert count == size : "Incorrect number of bins created in archive: " + count;
		}
		return labels;
	}


	@Override
	public int oneDimensionalIndex(int[] multi) {
		int binIndex = ( (multi[BIN_INDEX_NODES] - MIN_NUM_NEURONS) + (MAX_NUM_NEURONS - MIN_NUM_NEURONS + 1) * (multi[BIN_INDEX_LINKS] - MIN_NUM_LINKS));
		assert binIndex >= 0 : "Negative index "+Arrays.toString(multi) + " -> " + binIndex;
		return binIndex;
	}
}
