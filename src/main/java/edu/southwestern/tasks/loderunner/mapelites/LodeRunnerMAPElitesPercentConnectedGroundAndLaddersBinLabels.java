package edu.southwestern.tasks.loderunner.mapelites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.evolution.mapelites.BaseBinLabels;

/**
 * creates a binning scheme for LodeRunner experiments using MAPElites
 * @author kdste
 *
 */
public class LodeRunnerMAPElitesPercentConnectedGroundAndLaddersBinLabels extends BaseBinLabels {
	List<String> labels = null;
	public static final int BINS_PER_DIMENSION = 10; //[0%-10%][10%-20%].....[90%-100%]
	public static final int SCALE_BY_FOUR = 4; //makes bins of 4 for ground and ladders 
	
	/**
	 * Creates bin labels based on percentages 
	 * @return List of bin labels 
	 */
	@Override
	public List<String> binLabels() {
		if(labels==null) {
			int size = BINS_PER_DIMENSION*BINS_PER_DIMENSION*BINS_PER_DIMENSION; //10x10x10=1000
			labels = new ArrayList<String>(size);
			for(int i = 0; i < BINS_PER_DIMENSION; i++) {//Connected
				for(int j = 0; j < BINS_PER_DIMENSION*SCALE_BY_FOUR; j+=SCALE_BY_FOUR) { //ground [0-4][4-8]...
					for(int k = 0; k < BINS_PER_DIMENSION*SCALE_BY_FOUR; k+=SCALE_BY_FOUR) { //ladders [0-4][4-8]...
						labels.add("Connected"+i+"0-"+(i+1)+"0Ground"+j+"-"+(j+SCALE_BY_FOUR)+"Ladders"+k+"-"+(k+SCALE_BY_FOUR));
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
		return new String[] {"Connected Percent", "Ground Percent", "Ladders Percent"};
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {BINS_PER_DIMENSION, BINS_PER_DIMENSION, BINS_PER_DIMENSION};
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		double percentConnected = (Double) keys.get("Connected Percent");
		double percentGround = (Double) keys.get("Ground Percent");
		double percentLadders = (Double) keys.get("Ladders Percent");
		
		double SCALE_GROUND_LADDERS = BINS_PER_DIMENSION/4.0; //scales by 1/4 of the dimension to go in steps of 4
		//gets correct indices for all dimensions based on percent and multiplied by 10 to be a non decimal 
		int connectedIndex = Math.min((int)(percentConnected*BINS_PER_DIMENSION), BINS_PER_DIMENSION-1); 		
		// ground scaling is frustrating. percentGroundseems to land between 0.1 and 0.43. So, subtract 0.1 to get to
		// 0.0 to 0.33, then multiply by 3 to get 0.0 to 0.99
		int groundIndex = Math.max(0, Math.min((int)((percentGround-0.1)*3*BINS_PER_DIMENSION), BINS_PER_DIMENSION-1));
		int laddersIndex = Math.min((int)(percentLadders*SCALE_GROUND_LADDERS*BINS_PER_DIMENSION), BINS_PER_DIMENSION-1);
		
		int[] dims = new int[] {connectedIndex, groundIndex, laddersIndex}; // connectivity, percent ground scaled, percent ladders scaled
		
		return dims;
	}

}
