package edu.southwestern.tasks.molecules.smiles;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.BaseBinLabels;
import edu.southwestern.parameters.Parameters;

public class MoleculeBondTypeCountsBinLabels extends BaseBinLabels {

	private static int maxSingleBonds = -1;
	private static int maxDoubleBonds = -1;
	private static int maxTripleBonds = -1;
	private static int totalBins = -1;
	private static List<String> labels = null;
	
	@Override
	public List<String> binLabels() {
		if(labels == null) {
			
			maxSingleBonds = Parameters.parameters.integerParameter("maxSingleBondsForBinLabels");
			maxDoubleBonds = Parameters.parameters.integerParameter("maxDoubleBondsForBinLabels");
			maxTripleBonds = Parameters.parameters.integerParameter("maxTripleBondsForBinLabels");
			totalBins = (maxSingleBonds+1) * (maxDoubleBonds+1) * (maxTripleBonds+1);
			
			labels = new ArrayList<String>(totalBins);
			for(int s = 0; s <= maxSingleBonds; s++) {
				for(int d = 0; d <= maxDoubleBonds; d++) {
					for(int t = 0; t <= maxTripleBonds; t++) {
						labels.add("S"+s+"D"+d+"T"+t);
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
		int binIndex = tripleBonds + (doubleBonds + singleBonds*(maxDoubleBonds+1))*(maxTripleBonds+1);
		return binIndex;
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		// If actual bond count exceeds the "max" then bin together
		int singles = Math.min(maxSingleBonds, (Integer) keys.get("Single Bond Count"));
		int doubles = Math.min(maxDoubleBonds, (Integer) keys.get("Double Bond Count"));
		int triples = Math.min(maxTripleBonds, (Integer) keys.get("Triple Bond Count"));
		return new int[] {singles, doubles, triples};
	}

	@Override
	public String[] dimensions() {
		return new String[] {"Single Bond Count", "Double Bond Count", "Triple Bond Count"};
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {maxSingleBonds+1,maxDoubleBonds+1,maxTripleBonds+1};
	}

	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		MMNEAT.main(("runNumber:1 randomSeed:1 watch:false trials:1 mu:10 base:molecules log:Molecules-TargetBondCounts "+
				 "saveTo:TargetBondCounts maxGens:50000 io:true netio:true mating:false "+
			 	 "task:edu.southwestern.tasks.molecules.MoleculeTask cleanFrequency:-1 saveAllChampions:true "+
				 "genotype:edu.southwestern.evolution.genotypes.SMILESStringGenotype "+
			 	 // MAP Elites settings added here
				 "ea:edu.southwestern.evolution.mapelites.MAPElites "+
				 "experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment "+
				 "steadyStateIndividualsPerGeneration:100 "+
				 "mapElitesBinLabels:edu.southwestern.tasks.molecules.smiles.MoleculeBondTypeCountsBinLabels "+
				 "steadyStateArchetypeSaving:false "+
			 	 // Fitness related
			 	 "smilesTargetMeltingPoint:179.44000148773193 smilesTargetBoilingPoint:379.65999603271484 "+
				 "moleculeTargetMeltingAndBoilingPointFitness:true").split(" "));		
	}
	
}
