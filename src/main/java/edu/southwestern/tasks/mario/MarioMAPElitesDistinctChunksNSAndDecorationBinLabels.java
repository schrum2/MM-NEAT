package edu.southwestern.tasks.mario;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;
/**
 * new binning scheme for distinct chunks, negative space, and decoration
 * 
 *
 */
public class MarioMAPElitesDistinctChunksNSAndDecorationBinLabels implements BinLabels {
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
						labels.add("DistinctSegments["+i+"]NS["+j+"0-"+(j+1)+"0]Decoration["+r+"0-"+(r+1)+"0]");
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
