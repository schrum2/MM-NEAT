package edu.southwestern.tasks.loderunner;

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
				for(int j = 0; j < BINS_PER_DIMENSION*4; j+=4) { //ground [0-4][4-8]...
					for(int k = 0; k < BINS_PER_DIMENSION*4; k+=4) { //ladders [0-4][4-8]...
						labels.add("Connected["+i+"0-"+(i+1)+"0]Ground["+j+"-"+(j+4)+"]Ladders["+k+"-"+(k+4)+"]");
					}
				}
			}
		}
		return labels;
	}

}
