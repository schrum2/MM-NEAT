package edu.southwestern.tasks.molecules.smiles;

import java.io.FileNotFoundException;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.generalmappings.CombinationBinLabels;

public class MoleculeAtomBranchBondBinLabels extends CombinationBinLabels {

	public MoleculeAtomBranchBondBinLabels() {
		super(new MoleculeAtomTypeCountsBinLabels(), new MoleculeBranchAndBondTypeBinLabels());
	}

	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {

		MMNEAT.main(("runNumber:1 randomSeed:1 watch:false trials:1 mu:10 base:molecules "+
				"log:Molecules-TESTAtomBranchBondComboTargetMelting351Boiling436C saveTo:TESTAtomBranchBondComboTargetMelting351Boiling436C "+
				"maxGens:20000 io:true netio:true mating:false task:edu.southwestern.tasks.molecules.MoleculeTask "+
				"cleanFrequency:-1 saveAllChampions:true qdScoreForJustOneBin:true mapElitesLogsOtherScoreHypervolume:false "+
				"ea:edu.southwestern.evolution.mapelites.MAPElites qdFullLoggingForEachOtherStat:false "+
				"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 "+
				"mapElitesBinLabels:edu.southwestern.tasks.molecules.smiles.MoleculeAtomBranchBondBinLabels "+
				"maxSingleBondsForBinLabels:10 maxDoubleBondsForBinLabels:7 steadyStateArchetypeSaving:false "+
				"genotype:edu.southwestern.evolution.genotypes.SMILESStringGenotype moleculeTargetMeltingAndBoilingPointFitness:true "+
				"smilesTargetMeltingPoint:351.05000209808350 smilesTargetBoilingPoint:436.09000205993652").split(" "));
	}	
	
}
