package edu.southwestern.tasks.mario;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;

public class MarioMAPElitesNoveltyDecorFrequencyAndLeniencyBinLabels implements BinLabels {

	List<String> labels = null;
	private int levelBinsPerDimension;
	private int noveltyBinsPerDimension;
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			levelBinsPerDimension = 10;//Parameters.parameters.integerParameter("marioGANLevelChunks");
			noveltyBinsPerDimension = 20;//Parameters.parameters.integerParameter("noveltyBinAmount");
			
			int size = (levelBinsPerDimension+1)*levelBinsPerDimension*levelBinsPerDimension;
			labels = new ArrayList<String>(size);
			for(int i = 0; i <= noveltyBinsPerDimension; i++) { // Distinct Segments
				for(int j = 0; j < levelBinsPerDimension; j++) { // Negative Space
					for(int r = -(levelBinsPerDimension/2); r < levelBinsPerDimension/2; r++) { // Leniency allows negative range
						labels.add("Novelty["+i+"]DecorFrequency["+j+"0-"+(j+1)+"0]Leniency["+r+"0-"+(r+1)+"0]");
					}
				}
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		int binIndex = (multi[0]*noveltyBinsPerDimension + multi[1])*levelBinsPerDimension + multi[2];
		return binIndex;
	}
	
	
	public static void main(String[] args) {
		MarioMAPElitesNoveltyDecorFrequencyAndLeniencyBinLabels test = new MarioMAPElitesNoveltyDecorFrequencyAndLeniencyBinLabels();
		test.binLabels();
		for (String s : test.binLabels()) {
			System.out.println(s);
		}
	}

}
