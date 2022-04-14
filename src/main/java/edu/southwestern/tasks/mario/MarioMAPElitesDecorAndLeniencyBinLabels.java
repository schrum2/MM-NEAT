package edu.southwestern.tasks.mario;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.BaseBinLabels;
import edu.southwestern.parameters.Parameters;

public class MarioMAPElitesDecorAndLeniencyBinLabels extends BaseBinLabels {
		
		List<String> labels = null;
		private int binsPerDimension;
			
		@Override
		public List<String> binLabels() {
			if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
				binsPerDimension = Parameters.parameters.integerParameter("marioStatBasedMEBinIntervals");
				
				int size = binsPerDimension*binsPerDimension;
				labels = new ArrayList<String>(size);
				for(int i = 0; i < binsPerDimension; i++) { // Decoration
					for(int r = -(binsPerDimension/2); r < binsPerDimension/2; r++) { // Leniency allows negative range
						labels.add("Decoration"+i+"-"+(i+1)+"Leniency"+r+"-"+(r+1));
					}
				}
			}
			return labels;
		}

		@Override
		public int oneDimensionalIndex(int[] multi) {
			int binIndex = multi[0]*binsPerDimension + multi[1];
			return binIndex;
		}

		@Override
		public String[] dimensions() {
			return new String[] {"Decoration Percent", "Leniency Percent"};
		}
		
		@Override
		public int[] dimensionSizes() {
			return new int[] {binsPerDimension, binsPerDimension};
		}

		public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
			MMNEAT.main("runNumber:701 randomSeed:0 base:mariolevelsdecorateleniency log:MarioLevelsDecorateLeniency-CMAME5Improvement saveTo:CMAME5Improvement marioGANLevelChunks:1 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:37 lambda:100 maxGens:100000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.CMAME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDecorAndLeniencyBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 numImprovementEmitters:5 numOptimizingEmitters:0 CMAMESigma:0.5 marioStatBasedMEBinIntervals:50".split(" "));
		}
		
		@Override
		public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
			@SuppressWarnings("unchecked")
			ArrayList<double[]> lastLevelStats = (ArrayList<double[]>) keys.get("Level Stats");	
			
			double ganChunks = Parameters.parameters.integerParameter("marioGANLevelChunks");
			
			double leniencyPercentage = (MarioLevelTask.sumStatScore(lastLevelStats, MarioLevelTask.LENIENCY_STAT_INDEX) / ganChunks)*8.0 + 0.5;
			double decorationPercentage = (MarioLevelTask.sumStatScore(lastLevelStats, MarioLevelTask.DECORATION_FREQUENCY_STAT_INDEX) / ganChunks);
			decorationPercentage *= 8.0;
			
			int leniencySumIndex = Math.min((int) (leniencyPercentage*binsPerDimension), binsPerDimension-1); //LENIENCY BIN INDEX
			int decorationBinIndex =  Math.min((int) (decorationPercentage*binsPerDimension), binsPerDimension-1); //decorationBinIndex
			
			return new int[] {decorationBinIndex, leniencySumIndex};
		}
}
