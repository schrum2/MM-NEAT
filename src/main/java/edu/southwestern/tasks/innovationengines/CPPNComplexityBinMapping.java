package edu.southwestern.tasks.innovationengines;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.networks.Network;

public class CPPNComplexityBinMapping<T extends Network> implements BinLabels {
	
	List<String> labels = null;
	public static final int BINS_PER_DIMENSION = 10; //[0%-10%][10%-20%].....[90%-100%]
	public static final int MAX_NUM_NEURONS = 35;
	public static final int MAX_NUM_LINKS = 35;
	public static final int MIN_NUM_NEURONS = 5;
	public static final int MIN_NUM_LINKS = 5;

	@Override
	public List<String> binLabels() {
		// TODO Auto-generated method stub
		if(labels ==  null) {
			int size = BINS_PER_DIMENSION*BINS_PER_DIMENSION;
			labels = new ArrayList<String>(size);
			for(int i = MIN_NUM_LINKS; i < MAX_NUM_LINKS; i++) {
				for(int j = MIN_NUM_NEURONS; j < MAX_NUM_NEURONS; j++) {
					labels.add("Neurons[" + i + "]links[" + j + "]");
				}
			}
		}
		
//		if(labels==null) {
//			int size = BINS_PER_DIMENSION*BINS_PER_DIMENSION*BINS_PER_DIMENSION; //10x10x10=1000
//			labels = new ArrayList<String>(size);
//			for(int i = 0; i < BINS_PER_DIMENSION; i++) {//Connected
//				for(int j = 0; j < BINS_PER_DIMENSION*SCALE_BY_FOUR; j+=SCALE_BY_FOUR) { //ground [0-4][4-8]...
//					for(int k = 0; k < BINS_PER_DIMENSION*SCALE_BY_FOUR; k+=SCALE_BY_FOUR) { //ladders [0-4][4-8]...
//						labels.add("Connected["+i+"0-"+(i+1)+"0]Ground["+j+"-"+(j+SCALE_BY_FOUR)+"]Ladders["+k+"-"+(k+SCALE_BY_FOUR)+"]");
//					}
//				}
//			}
//		}
//		return labels;
//	}

		return null;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		// TODO Auto-generated method stub
		return 0;
	}

}
