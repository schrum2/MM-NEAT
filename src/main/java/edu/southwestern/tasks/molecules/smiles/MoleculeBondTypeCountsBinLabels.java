package edu.southwestern.tasks.molecules.smiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.evolution.mapelites.BaseBinLabels;

public class MoleculeBondTypeCountsBinLabels extends BaseBinLabels {

	private static final int MAX_SINGLE_BONDS = 15;
	private static final int MAX_DOUBLE_BONDS = 10;
	private static final int MAX_TRIPLE_BONDS = 5;
	private static final int TOTAL_BINS = (MAX_SINGLE_BONDS+1) * (MAX_DOUBLE_BONDS+1) * (MAX_TRIPLE_BONDS+1);
	List<String> labels = null;
	
	@Override
	public List<String> binLabels() {
		if(labels == null) {
			labels = new ArrayList<String>(TOTAL_BINS);
			for(int s = 0; s <= MAX_SINGLE_BONDS; s++) {
				for(int d = 0; d <= MAX_DOUBLE_BONDS; d++) {
					for(int t = 0; t <= MAX_TRIPLE_BONDS; t++) {
						labels.add("-"+s+"="+d+"#"+t);
					}
				}
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		int singleBonds = multi[0];
		int doubleBonds = multi[1];
		int tripleBonds = multi[2];
		int binIndex = tripleBonds + (doubleBonds + singleBonds*(MAX_DOUBLE_BONDS+1))*(MAX_TRIPLE_BONDS+1);
		return binIndex;
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		// If actual bond count exceeds the "max" then bin together
		int singles = Math.min(MAX_SINGLE_BONDS, (Integer) keys.get("Single Bond Count"));
		int doubles = Math.min(MAX_DOUBLE_BONDS, (Integer) keys.get("Double Bond Count"));
		int triples = Math.min(MAX_TRIPLE_BONDS, (Integer) keys.get("Triple Bond Count"));
		return new int[] {singles, doubles, triples};
	}

	@Override
	public String[] dimensions() {
		return new String[] {"Single Bond Count", "Double Bond Count", "Triple Bond Count"};
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {MAX_SINGLE_BONDS+1,MAX_DOUBLE_BONDS+1,MAX_TRIPLE_BONDS+1};
	}

}
