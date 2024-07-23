package edu.southwestern.tasks.molecules.smiles;

import java.io.FileNotFoundException;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.generalmappings.CombinationBinLabels;

public class MoleculeAtomAndBondTypeBinLabels extends CombinationBinLabels {

	public MoleculeAtomAndBondTypeBinLabels() {
		super(new MoleculeAtomTypeCountsBinLabels(), new MoleculeBondTypeCountsBinLabels());
	}

	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		MMNEAT.main(("runNumber:2 randomSeed:2 watch:false trials:1 mu:10 base:molecules log:Molecules-TargetAtomAndBondCounts "+
				 "saveTo:TargetAtomAndBondCounts maxGens:50000 io:true netio:true mating:false "+
			 	 "task:edu.southwestern.tasks.molecules.MoleculeTask cleanFrequency:-1 saveAllChampions:true "+
				 "genotype:edu.southwestern.evolution.genotypes.SMILESStringGenotype "+
			 	 // MAP Elites settings added here
				 "ea:edu.southwestern.evolution.mapelites.MAPElites "+
				 "experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment "+
				 "steadyStateIndividualsPerGeneration:100 "+
				 "mapElitesBinLabels:edu.southwestern.tasks.molecules.smiles.MoleculeAtomAndBondTypeBinLabels "+
				 "steadyStateArchetypeSaving:false "+
			 	 // Fitness related
			 	 "smilesTargetMeltingPoint:179.44000148773193 smilesTargetBoilingPoint:379.65999603271484 "+
				 "moleculeTargetMeltingAndBoilingPointFitness:true").split(" "));		
	}	
	
}
