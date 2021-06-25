package edu.southwestern.tasks.mario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.evolution.mapelites.BaseBinLabels;
import edu.southwestern.evolution.mapelites.generalmappings.TileNoveltyBinLabels;
import edu.southwestern.parameters.Parameters;

/**
 * Binning scheme for Mario GAN using Level
 * Novelty, Decoration Frequency, and Leniency
 * 
 * @author Maxx Batterton
 *
 */
public class MarioMAPElitesNoveltyDecorAndLeniencyBinLabels extends BaseBinLabels {

	List<String> labels = null;
	private int levelBinsPerDimension; // amount of bins for the Decor and Leniency dimensions
	private int noveltyBinsPerDimension; // amount of bins for the Novelty dimension
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			levelBinsPerDimension = Parameters.parameters.integerParameter("marioGANLevelChunks");
			noveltyBinsPerDimension = Parameters.parameters.integerParameter("noveltyBinAmount");
			
			int size = (levelBinsPerDimension+1)*levelBinsPerDimension*levelBinsPerDimension;
			labels = new ArrayList<String>(size);
			for(int i = 0; i < noveltyBinsPerDimension; i++) { // Novelty Segments
				for(int j = 0; j < levelBinsPerDimension; j++) { // Negative Space
					for(int r = -(levelBinsPerDimension/2); r < levelBinsPerDimension/2; r++) { // Leniency allows negative range
						labels.add("Novelty"+((double) i/noveltyBinsPerDimension)+"-"+((double) (i+1)/noveltyBinsPerDimension)+"DecorFrequency"+j+"0-"+(j+1)+"0Leniency"+r+"0-"+(r+1)+"0");
					}
				}
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		int binIndex = (int) ((multi[0])*levelBinsPerDimension + (multi[1])*levelBinsPerDimension + multi[2]);
		return binIndex;
	}	
	
	@Override
	public String[] dimensions() {
		return new String[] {"Novelty", "Sum Decoration", "Sum Leniency"};
	}
	
	@Override
	public int[] dimensionSizes() {
		return new int[] {noveltyBinsPerDimension, levelBinsPerDimension, levelBinsPerDimension};
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		@SuppressWarnings("unchecked")
		ArrayList<double[]> lastLevelStats = (ArrayList<double[]>) keys.get("Level Stats");		
		@SuppressWarnings("unchecked")
		List<List<Integer>> level = (List<List<Integer>>) keys.get("Level");
		
		final double DECORATION_SCALE = 3;// Scale scores so that we are less likely to overstep the bounds of the bins
		final int BINS_PER_DIMENSION = Parameters.parameters.integerParameter("marioGANLevelChunks");
		final int NOVELTY_BINS_PER_DIMENSION = Parameters.parameters.integerParameter("noveltyBinAmount");
		
		double leniencySum = MarioLevelTask.sumStatScore(lastLevelStats, MarioLevelTask.LENIENCY_STAT_INDEX);
		double decorationSum = MarioLevelTask.sumStatScore(lastLevelStats, MarioLevelTask.DECORATION_FREQUENCY_STAT_INDEX);
		double novelty = TileNoveltyBinLabels.levelNovelty(level);
		
		int leniencySumIndex = Math.min(Math.max((int)((leniencySum*(BINS_PER_DIMENSION/2)+0.5)*BINS_PER_DIMENSION),0), BINS_PER_DIMENSION-1); //LEANIENCY BIN INDEX
		int decorationBinIndex =  Math.min((int)(decorationSum*DECORATION_SCALE*BINS_PER_DIMENSION), BINS_PER_DIMENSION-1); //decorationBinIndex
		int noveltyIndex =  Math.min((int)(novelty*NOVELTY_BINS_PER_DIMENSION), NOVELTY_BINS_PER_DIMENSION-1);
			
		return new int[] {noveltyIndex, decorationBinIndex, leniencySumIndex};
	}
}
