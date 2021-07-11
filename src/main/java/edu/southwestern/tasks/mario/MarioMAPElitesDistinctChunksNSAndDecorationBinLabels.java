package edu.southwestern.tasks.mario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.evolution.mapelites.BaseBinLabels;
import edu.southwestern.parameters.Parameters;
/**
 * new binning scheme for distinct chunks, negative space, and decoration
 * 
 * Known as Distinct ASAD in ToG journal paper
 */
public class MarioMAPElitesDistinctChunksNSAndDecorationBinLabels extends BaseBinLabels {
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
					for(int r = 0; r < binsPerDimension; r++) { // Decoration frequency
						labels.add("DistinctSegments"+i+"NS"+j+"0-"+(j+1)+"0Decoration"+r+"0-"+(r+1)+"0");
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
		return new String[] {"Distinct Segments", "Alternating Negative Space", "Alternating Decoration Frequency"};
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
		
		assert Parameters.parameters.integerParameter("marioGANLevelChunks") > 1 : "Can't have variation with MarioMAPElitesDistinctChunksNSAndDecorationBinLabels bin scheme if marioGANLevelChunks:1 is set!";
		double decorationAlternating = MarioLevelTask.alternatingStatScore(lastLevelStats, MarioLevelTask.DECORATION_FREQUENCY_STAT_INDEX);
		double negativeSpaceAlternating = MarioLevelTask.alternatingStatScore(lastLevelStats, MarioLevelTask.NEGATIVE_SPACE_STAT_INDEX);
		
		// Lower the scale when using alternating score
		final int BINS_PER_DIMENSION = Parameters.parameters.integerParameter("marioGANLevelChunks");
		final double DECORATION_SCALE = 0.2;
		final double NEGATIVE_SPACE_SCALE = 0.85;
		
		int decorationBinIndex = Math.min((int)(decorationAlternating*DECORATION_SCALE*BINS_PER_DIMENSION*10), BINS_PER_DIMENSION-1);
		int negativeSpaceIndex = Math.min((int)(negativeSpaceAlternating*NEGATIVE_SPACE_SCALE*BINS_PER_DIMENSION), BINS_PER_DIMENSION-1);
		
		return new int[] {numDistinctSegments, negativeSpaceIndex, decorationBinIndex};
	}
}
