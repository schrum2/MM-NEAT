package edu.southwestern.tasks.mario;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;
/**
 * new binning scheme for Mario to include uniqueness of segments
 * Also includes bins for negative space and leniency
 *
 *
 */
public class MarioMAPElitesDistinctChunksNSAndLeniencyBinLabels implements BinLabels {
	
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
						labels.add("DistinctSegments["+i+"]NS["+j+"0-"+(j+1)+"0]Leniency["+r+"0-"+(r+1)+"0]");
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
