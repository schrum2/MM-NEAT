package edu.southwestern.tasks.molecules.smiles;

import edu.southwestern.evolution.mapelites.generalmappings.CombinationBinLabels;

public class MoleculeBranchAndBondTypeBinLabels extends CombinationBinLabels {

	public MoleculeBranchAndBondTypeBinLabels() {
		super(new MoleculeBranchCountsBinLabels(), new MoleculeBondTypeCountsBinLabels());
	}


}
