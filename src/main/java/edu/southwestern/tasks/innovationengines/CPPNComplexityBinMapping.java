package edu.southwestern.tasks.innovationengines;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.networks.Network;

public class CPPNComplexityBinMapping<T extends Network> implements BinLabels {
	
	List<String> labels = null;
	public static final int BIN_INDEX_NODES = 0;
	public static final int BIN_INDEX_LINKS = 1;
	
	public static final int MAX_NUM_NEURONS = 35;
	public static final int MAX_NUM_LINKS = 35;
	public static final int MIN_NUM_NEURONS = 5;
	public static final int MIN_NUM_LINKS = 5;

	@Override
	public List<String> binLabels() {
		if(labels ==  null) {
			int size = (MAX_NUM_NEURONS - MIN_NUM_NEURONS + 1)*(MAX_NUM_LINKS - MIN_NUM_LINKS + 1);
			labels = new ArrayList<String>(size);
			for(int i = MIN_NUM_LINKS; i < MAX_NUM_LINKS; i++) {
				for(int j = MIN_NUM_NEURONS; j < MAX_NUM_NEURONS; j++) {
					labels.add("Neurons[" + j + "]links[" + i + "]");
				}
			}
		}
		return labels;
	}


	@Override
	public int oneDimensionalIndex(int[] multi) {
		//int binIndex = (multi[0]*BINS_PER_DIMENSION + multi[1])*BINS_PER_DIMENSION + multi[2];
		int binIndex = (multi[BIN_INDEX_NODES] + (MAX_NUM_NEURONS - MIN_NUM_NEURONS + 1) * multi[BIN_INDEX_LINKS]);
		return binIndex;
	}
}
