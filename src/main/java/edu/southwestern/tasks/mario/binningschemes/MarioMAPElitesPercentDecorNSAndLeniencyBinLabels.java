package edu.southwestern.tasks.mario.binningschemes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.evolution.mapelites.BaseBinLabels;
import edu.southwestern.tasks.mario.level.LevelParser;

/**
 * General binning scheme that does not require level to consist of segments.
 * 
 * @author Jacob Schrum
 *
 */
public class MarioMAPElitesPercentDecorNSAndLeniencyBinLabels extends BaseBinLabels {

	List<String> labels = null;
	private int binsPerDimension = 10; // Will this change later or be final?
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			int size = binsPerDimension*binsPerDimension*binsPerDimension;
			labels = new ArrayList<String>(size);
			for(int i = 0; i < binsPerDimension; i++) { // Decoration
				for(int j = 0; j < binsPerDimension; j++) { // Negative Space
					for(int r = -(binsPerDimension/2); r < binsPerDimension/2; r++) { // Leniency allows negative range
						labels.add("Decoration"+i+"0-"+(i+1)+"0NS"+j+"0-"+(j+1)+"0Leniency"+r+"0-"+(r+1)+"0");
					}
				}
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		int binIndex = (multi[0]*binsPerDimension + multi[1])*binsPerDimension + multi[2];
		assert binIndex < labels.size() : binIndex+":"+Arrays.toString(multi)+":"+binsPerDimension;
		return binIndex;
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		
		double[] stats = (double[]) keys.get("Complete Stats");
		
		//System.out.println(Arrays.toString(stats));
		
		double decorationPercent = stats[LevelParser.LEVEL_STATS_DECORATION_INDEX];
		double leniencyPercent = stats[LevelParser.LEVEL_STATS_LENIENCY_INDEX];
		double spaceCoveredPercent = stats[LevelParser.LEVEL_STATS_SPACE_COVERAGE_INDEX];

		final double SCALE_FACTOR = 13;
		int decorationBinIndex = (int) (SCALE_FACTOR * decorationPercent * binsPerDimension);
		int spaceCoveredBinIndex = (int) (SCALE_FACTOR * spaceCoveredPercent * binsPerDimension);
		// leniencyPercent is probably in the range -0.5 to 0.5, so shift to 0.0 to 1.0 first
		int leniencyBinIndex = (int) ((leniencyPercent + 0.5) * binsPerDimension);
		leniencyBinIndex = Math.min(leniencyBinIndex, binsPerDimension - 1);
		leniencyBinIndex = Math.max(leniencyBinIndex, 0);
		
		assert decorationBinIndex < binsPerDimension : decorationBinIndex+" of "+binsPerDimension;
		assert spaceCoveredBinIndex < binsPerDimension : spaceCoveredBinIndex+" of "+binsPerDimension;
		assert leniencyBinIndex < binsPerDimension : leniencyBinIndex+" of "+binsPerDimension;
		assert 0 <= leniencyBinIndex : leniencyBinIndex+" of "+binsPerDimension;
		
		return new int[] {decorationBinIndex, spaceCoveredBinIndex, leniencyBinIndex};
	}

	@Override
	public String[] dimensions() {
		return new String[] {"Decoration", "Negative Space", "Leniency"};
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {binsPerDimension, binsPerDimension, binsPerDimension};
	}

}
