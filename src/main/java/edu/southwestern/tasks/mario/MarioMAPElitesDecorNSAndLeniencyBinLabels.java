package edu.southwestern.tasks.mario;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;

public class MarioMAPElitesDecorNSAndLeniencyBinLabels implements BinLabels {
	
	List<String> labels = null;
	private int binsPerDimension;
		
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			binsPerDimension = Parameters.parameters.integerParameter("marioGANLevelChunks");
			
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
		return binIndex;
	}

	@Override
	public String[] dimensions() {
		return new String[] {"Decoration", "Negative Space", "Leniency"};
	}
	
	@Override
	public int[] dimensionSizes() {
		return new int[] {binsPerDimension, binsPerDimension, binsPerDimension};
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		MMNEAT.main("runNumber:1 randomSeed:1 base:mariolevelsdecoratensleniency log:MarioLevelsDecorateNSLeniency-ME saveTo:ME marioGANLevelChunks:10 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:100 maxGens:100000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDecorNSAndLeniencyBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000".split(" "));
	}
}