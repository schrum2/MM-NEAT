package edu.southwestern.tasks.molecules.smiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.evolution.mapelites.BaseBinLabels;
import edu.southwestern.parameters.Parameters;

public class MoleculeAtomTypeCountsBinLabels extends BaseBinLabels {

	private static int maxAtoms = -1;
	private static int totalBins = -1;
	private static List<String> labels = null;
	
	@Override
	public List<String> binLabels() {
		if(labels == null) {
			
			maxAtoms = Parameters.parameters.integerParameter("maxAtomsForAtomTypeBinLabels");
			totalBins = (maxAtoms+1) * (maxAtoms+1) * (maxAtoms+1);
			
			labels = new ArrayList<String>(totalBins);
			for(int c = 0; c <= maxAtoms; c++) {
				for(int o = 0; o <= maxAtoms; o++) {
					for(int n = 0; n <= maxAtoms; n++) {
						labels.add("C"+c+"O"+o+"N"+n);
					}
				}
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		int carbonAtoms = multi[0];
		int oxygenAtoms = multi[1];
		int nitrogenAtoms = multi[2];
		int binIndex = nitrogenAtoms + (oxygenAtoms + carbonAtoms*(maxAtoms+1))*(maxAtoms+1);
		return binIndex;
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		// If actual atom count exceeds the "max" then bin together
		int carbons = Math.min(maxAtoms, (Integer) keys.get("Carbon Count"));
		int oxygens = Math.min(maxAtoms, (Integer) keys.get("Oxygen Count"));
		int nitrogens = Math.min(maxAtoms, (Integer) keys.get("Nitrogen Count"));
		return new int[] {carbons, oxygens, nitrogens};
	}

	@Override
	public String[] dimensions() {
		return new String[] {"Carbon Count", "Oxygen Count", "Nitrogen Count"};
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {maxAtoms+1,maxAtoms+1,maxAtoms+1};
	}

}
