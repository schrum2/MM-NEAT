package edu.southwestern.tasks.molecules.smiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.evolution.mapelites.BaseBinLabels;

public class MoleculeAtomTypeCountsBinLabels extends BaseBinLabels {

	private static final int MAX_ATOMS = 10;
	private static final int TOTAL_BINS = (MAX_ATOMS+1) * (MAX_ATOMS+1) * (MAX_ATOMS+1);
	List<String> labels = null;
	
	@Override
	public List<String> binLabels() {
		if(labels == null) {
			labels = new ArrayList<String>(TOTAL_BINS);
			for(int c = 0; c <= MAX_ATOMS; c++) {
				for(int o = 0; o <= MAX_ATOMS; o++) {
					for(int n = 0; n <= MAX_ATOMS; n++) {
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
		int binIndex = nitrogenAtoms + (oxygenAtoms + carbonAtoms*(MAX_ATOMS+1))*(MAX_ATOMS+1);
		return binIndex;
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		int carbons = (Integer) keys.get("Carbon Count");
		int oxygens = (Integer) keys.get("Oxygen Count");
		int nitrogens = (Integer) keys.get("Nitrogen Count");
		return new int[] {carbons, oxygens, nitrogens};
	}

	@Override
	public String[] dimensions() {
		return new String[] {"Carbon Count", "Oxygen Count", "Nitrogen Count"};
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {MAX_ATOMS+1,MAX_ATOMS+1,MAX_ATOMS+1};
	}

}
