package edu.southwestern.tasks.loderunner.mapelites;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;

/**
 * creates a binning scheme for LodeRunner experiments using MAPElites
 * @author kdste
 *
 */
public class LodeRunnerMAPElitesPercentConnectedGroundAndLaddersBinLabels implements BinLabels{
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
						labels.add("Connected["+i+"0-"+(i+1)+"0]Ground["+j+"-"+(j+SCALE_BY_FOUR)+"]Ladders["+k+"-"+(k+SCALE_BY_FOUR)+"]");
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
