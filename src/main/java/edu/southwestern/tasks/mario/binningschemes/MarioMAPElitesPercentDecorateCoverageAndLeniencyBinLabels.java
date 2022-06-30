package edu.southwestern.tasks.mario.binningschemes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.evolution.mapelites.BaseBinLabels;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.level.LevelParser;

/**
 * General binning scheme that does not require level to consist of segments.
 * 
 * @author Jacob Schrum
 *
 */
public class MarioMAPElitesPercentDecorateCoverageAndLeniencyBinLabels extends BaseBinLabels {

	List<String> labels = null;
	private int binsPerDimension = 10; // Will this change later or be final?
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			int size = binsPerDimension*binsPerDimension*binsPerDimension;
			labels = new ArrayList<String>(size);
			for(int i = 0; i < binsPerDimension; i++) { // Decoration
				for(int r = -(binsPerDimension/2); r < binsPerDimension/2; r++) { // Leniency allows negative range
					for(int j = 0; j < binsPerDimension; j++) { // Space Coverage
						labels.add("Decoration"+i+"0-"+(i+1)+"0Coverage"+j+"0-"+(j+1)+"0Leniency"+r+"0-"+(r+1)+"0");
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

		double scaledLeniency = SCALE_FACTOR * leniencyPercent * binsPerDimension;
		int leniencyBinIndex = (int) Math.floor((binsPerDimension/2) + scaledLeniency);
		//System.out.println("Leniency: "+ leniencyPercent + " -> " + scaledLeniency + " -> " + leniencyBinIndex);
		leniencyBinIndex = Math.min(leniencyBinIndex, binsPerDimension - 1);
		leniencyBinIndex = Math.max(leniencyBinIndex, 0);
		
		assert decorationBinIndex < binsPerDimension : decorationBinIndex+" of "+binsPerDimension;
		assert spaceCoveredBinIndex < binsPerDimension : spaceCoveredBinIndex+" of "+binsPerDimension;
		assert leniencyBinIndex < binsPerDimension : leniencyBinIndex+" of "+binsPerDimension;
		assert 0 <= leniencyBinIndex : leniencyBinIndex+" of "+binsPerDimension;
		
		return new int[] {decorationBinIndex, leniencyBinIndex, spaceCoveredBinIndex};
	}

	@Override
	public String[] dimensions() {
		return new String[] {"Decoration", "Leniency", "Space Coverage"};
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {binsPerDimension, binsPerDimension, binsPerDimension};
	}

	@Override
	public boolean discard(HashMap<String, Object> behaviorMap) {
		int[] multi = multiDimensionalIndices(behaviorMap);
		// Allow for discarding of solutions outside of a restricted range set at the command line
		boolean result =
			   multi[LevelParser.LEVEL_STATS_DECORATION_INDEX] < Parameters.parameters.integerParameter("marioMinDecorationIndex") ||
			   multi[LevelParser.LEVEL_STATS_DECORATION_INDEX] > Parameters.parameters.integerParameter("marioMaxDecorationIndex") ||
			   multi[LevelParser.LEVEL_STATS_LENIENCY_INDEX] < Parameters.parameters.integerParameter("marioMinLeniencyIndex") ||
			   multi[LevelParser.LEVEL_STATS_LENIENCY_INDEX] > Parameters.parameters.integerParameter("marioMaxLeniencyIndex") ||
			   multi[LevelParser.LEVEL_STATS_SPACE_COVERAGE_INDEX] < Parameters.parameters.integerParameter("marioMinSpaceCoverageIndex") ||
			   multi[LevelParser.LEVEL_STATS_SPACE_COVERAGE_INDEX] > Parameters.parameters.integerParameter("marioMaxSpaceCoverageIndex");
			   
//		if(result) {
//			System.out.println("Discarding "+Arrays.toString(multi)+" from "+Arrays.toString((double[]) behaviorMap.get("Complete Stats")));
//			if(multi[LevelParser.LEVEL_STATS_DECORATION_INDEX] < Parameters.parameters.integerParameter("marioMinDecorationIndex")) System.out.println("Decoration low");
//			if(multi[LevelParser.LEVEL_STATS_DECORATION_INDEX] > Parameters.parameters.integerParameter("marioMaxDecorationIndex")) System.out.println("Decoration high");
//			if(multi[LevelParser.LEVEL_STATS_LENIENCY_INDEX] < Parameters.parameters.integerParameter("marioMinLeniencyIndex")) System.out.println("Leniency low");
//			if(multi[LevelParser.LEVEL_STATS_LENIENCY_INDEX] > Parameters.parameters.integerParameter("marioMaxLeniencyIndex")) System.out.println("Leniency high");
//			if(multi[LevelParser.LEVEL_STATS_SPACE_COVERAGE_INDEX] < Parameters.parameters.integerParameter("marioMinSpaceCoverageIndex")) System.out.println("Space low");
//			if(multi[LevelParser.LEVEL_STATS_SPACE_COVERAGE_INDEX] > Parameters.parameters.integerParameter("marioMaxSpaceCoverageIndex")) System.out.println("Space high");
//		}
		return result;
	}
}
