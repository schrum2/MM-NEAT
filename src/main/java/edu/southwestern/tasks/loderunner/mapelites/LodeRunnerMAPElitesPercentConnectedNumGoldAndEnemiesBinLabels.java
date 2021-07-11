package edu.southwestern.tasks.loderunner.mapelites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.evolution.mapelites.BaseBinLabels;

public class LodeRunnerMAPElitesPercentConnectedNumGoldAndEnemiesBinLabels extends BaseBinLabels {
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
						labels.add("Connected"+i+"0-"+(i+1)+"0Treasure"+j+"-"+(j+SCALE_BY_FIVE)+"Enemies"+k+"-"+(k+SCALE_BY_TWO));
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

	@Override
	public String[] dimensions() {
		return new String[] {"Connected Percent", "Treasures", "Enemies"};
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {BINS_PER_DIMENSION, BINS_PER_DIMENSION, BINS_PER_DIMENSION};
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		double percentConnected = (Double) keys.get("Connected Percent");
		double numTreasure = (Double) keys.get("Treasures");
		double numEnemies = (Double) keys.get("Enemies");
		
		//gets correct indices for all dimensions based on percent and multiplied by 10 to be a non decimal 
		int connectedIndex = Math.min((int)(percentConnected*BINS_PER_DIMENSION), BINS_PER_DIMENSION-1); 		
		
		double treasureScale = 5.0; //scales bins to be in groups of 5, [0-5][5-10]...
		double enemyScale = 2.0; //scales bins to be in groups of 2, [0-2][2-4]...
		//gets correct indices for treasure and enemies
		int treasureIndex = (int) Math.min(numTreasure/treasureScale, BINS_PER_DIMENSION-1);
		int enemyIndex = (int) Math.min(numEnemies/enemyScale, BINS_PER_DIMENSION-1);
		return new int[] {connectedIndex, treasureIndex, enemyIndex}; // connectivity, number of treasures scaled, number of enemies scaled
	}
}
