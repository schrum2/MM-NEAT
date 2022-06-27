package edu.southwestern.tasks.mario.binningschemes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.evolution.mapelites.BaseBinLabels;

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
		// TODO Auto-generated method stub
		return null;
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
