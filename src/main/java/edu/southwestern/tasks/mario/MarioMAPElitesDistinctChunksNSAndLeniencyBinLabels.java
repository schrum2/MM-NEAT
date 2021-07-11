package edu.southwestern.tasks.mario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.evolution.mapelites.BaseBinLabels;
import edu.southwestern.parameters.Parameters;
/**
 * new binning scheme for Mario to include uniqueness of segments
 * Also includes bins for negative space and leniency
 *
 * Not used in any papers as of June 2021
 */
public class MarioMAPElitesDistinctChunksNSAndLeniencyBinLabels extends BaseBinLabels {
	
	List<String> labels = null;
	private int binsPerDimension;
		
	@Override
	/**
	 * sets the bin labels
	 */
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			binsPerDimension = Parameters.parameters.integerParameter("marioGANLevelChunks");
			
			int size = (binsPerDimension+1)*binsPerDimension*binsPerDimension;
			labels = new ArrayList<String>(size);
			for(int i = 0; i <= binsPerDimension; i++) { // Distinct Segments
				for(int j = 0; j < binsPerDimension; j++) { // Negative Space
					for(int r = -(binsPerDimension/2); r < binsPerDimension/2; r++) { // Leniency allows negative range
						labels.add("DistinctSegments"+i+"NS"+j+"0-"+(j+1)+"0Leniency"+r+"0-"+(r+1)+"0");
					}
				}
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		int binIndex = (multi[0]*binsPerDimension + multi[1])*binsPerDimension + multi[2];
		return binIndex;
	}
	
	@Override
	public String[] dimensions() {
		return new String[] {"Distinct Segments", "Sum Negative Space", "Sum Leniency"};
	}
	
	@Override
	public int[] dimensionSizes() {
		return new int[] {binsPerDimension+1, binsPerDimension, binsPerDimension};
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		@SuppressWarnings("unchecked")
		ArrayList<double[]> lastLevelStats = (ArrayList<double[]>) keys.get("Level Stats");		
		int numDistinctSegments = (int) keys.get("Distinct Segments");
		
		double leniencySum = MarioLevelTask.sumStatScore(lastLevelStats, MarioLevelTask.LENIENCY_STAT_INDEX);
		double negativeSpaceSum = MarioLevelTask.sumStatScore(lastLevelStats, MarioLevelTask.NEGATIVE_SPACE_STAT_INDEX);

		double NEGATIVE_SPACE_SCALE = 3;
		// Scale scores so that we are less likely to overstep the bounds of the bins
		final int BINS_PER_DIMENSION = Parameters.parameters.integerParameter("marioGANLevelChunks");
		
		int leniencySumIndex = Math.min(Math.max((int)((leniencySum*(BINS_PER_DIMENSION/2)+0.5)*BINS_PER_DIMENSION),0), BINS_PER_DIMENSION-1); //LEANIENCY BIN INDEX
		int negativeSpaceSumIndex = Math.min((int)(negativeSpaceSum*NEGATIVE_SPACE_SCALE*BINS_PER_DIMENSION), BINS_PER_DIMENSION-1); //negative space index

	
		return new int[] {numDistinctSegments, negativeSpaceSumIndex, leniencySumIndex};
	}
}
