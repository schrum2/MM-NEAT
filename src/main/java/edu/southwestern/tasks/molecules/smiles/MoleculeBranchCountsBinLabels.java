package edu.southwestern.tasks.molecules.smiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.evolution.mapelites.BaseBinLabels;
import edu.southwestern.parameters.Parameters;

public class MoleculeBranchCountsBinLabels extends BaseBinLabels {

	private static int maxBranches = -1;
	private static List<String> labels = null;
	
	@Override
	public List<String> binLabels() {
		if(labels == null) {
			
			maxBranches = Parameters.parameters.integerParameter("maxBranchesForBinLabels");
			
			labels = new ArrayList<String>(maxBranches+1);
			for(int b = 0; b <= maxBranches; b++) {
				labels.add("B"+b);
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		return multi[0]; // already 1D
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		// If actual count exceeds the "max" then bin together
		int count = Math.min(maxBranches, (Integer) keys.get("Branch Count"));
		return new int[] {count};
	}

	@Override
	public String[] dimensions() {
		return new String[] {"Branch Count"};
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {maxBranches+1};
	}

}
