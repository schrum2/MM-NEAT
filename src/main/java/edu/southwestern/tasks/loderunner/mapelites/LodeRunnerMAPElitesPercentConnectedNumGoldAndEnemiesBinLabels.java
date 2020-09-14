package edu.southwestern.tasks.loderunner.mapelites;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;

public class LodeRunnerMAPElitesPercentConnectedNumGoldAndEnemiesBinLabels implements BinLabels{
	List<String> labels = null;
	public static final int BINS_PER_DIMENSION = 10; //[0%-10%][10%-20%].....[90%-100%]
	public static final int SCALE_BY_FIVE = 5; //makes groups of 5
	public static final int SCALE_BY_TWO = 2; //makes groups of 2
	
	@Override
	public List<String> binLabels() {
		if(labels==null) {
			int size = BINS_PER_DIMENSION*BINS_PER_DIMENSION*BINS_PER_DIMENSION; //10x10x10=1000
			labels = new ArrayList<String>(size);
			for(int i = 0; i < BINS_PER_DIMENSION; i++) { //connectivity
				for(int j = 0; j < BINS_PER_DIMENSION*SCALE_BY_FIVE; j+=SCALE_BY_FIVE) { //number of treasure
					for(int k = 0; k < BINS_PER_DIMENSION*SCALE_BY_TWO; k+=SCALE_BY_TWO) { //number of enemies 
						labels.add("Connected["+i+"0-"+(i+1)+"0]Treasure["+j+"-"+(j+SCALE_BY_FIVE)+"]Enemies["+k+"-"+(k+SCALE_BY_TWO)+"]");
					}
				}
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		int binIndex = (multi[0]*BINS_PER_DIMENSION + multi[1])*BINS_PER_DIMENSION + multi[2];
		return binIndex;
	}

}
