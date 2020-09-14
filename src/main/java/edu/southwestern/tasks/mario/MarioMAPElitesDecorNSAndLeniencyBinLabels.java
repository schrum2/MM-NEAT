package edu.southwestern.tasks.mario;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;

public class MarioMAPElitesDecorNSAndLeniencyBinLabels implements BinLabels {
	
	List<String> labels = null;
	private int binsPerDimension;
		
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			binsPerDimension = Parameters.parameters.integerParameter("marioGANLevelChunks");
			
			int size = binsPerDimension*binsPerDimension*binsPerDimension;
			labels = new ArrayList<String>(size);
			for(int i = 0; i < binsPerDimension; i++) { // Decoration
				for(int j = 0; j < binsPerDimension; j++) { // Negative Space
					for(int r = -(binsPerDimension/2); r < binsPerDimension/2; r++) { // Leniency allows negative range
						labels.add("Decoration["+i+"0-"+(i+1)+"0]NS["+j+"0-"+(j+1)+"0]Leniency["+r+"0-"+(r+1)+"0]");
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

}