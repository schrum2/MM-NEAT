package edu.southwestern.evolution.mapelites.generalmappings;

import java.io.FileNotFoundException;
import java.util.HashMap;

import distance.convolution.ConvNTuple;
import distance.kl.KLDiv;
import distance.test.KLDivTest;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;

/**
 * Binning scheme for KL Divergence
 * 
 * @author Maxx Batterton
 *
 */
public class KLDivergenceBinLabels extends MultiDimensionalRealValuedBinLabels {
	
	private int klDivBinDimension;
	
	public KLDivergenceBinLabels() {
		super(Parameters.parameters.integerParameter("klDivBinDimension"), 0, Parameters.parameters.doubleParameter("klDivMaxValue"), 2, 1);
		this.klDivBinDimension = Parameters.parameters.integerParameter("klDivBinDimension");
	}
	
	/**
	 * Get the behaviorCharacterization of a provided level
	 * with the task specified  
	 * 
	 * @param levels
	 * @return
	 */
	public static double[] behaviorCharacterization(int[][] solutionLevel, int[][][] dimensionLevels) {
		double[] bc = new double[dimensionLevels.length];
		for (int i = 0; i < dimensionLevels.length; i++) {
			bc[i] = getKLDivergence(solutionLevel, dimensionLevels[i]);
		}
		return bc;
	}
	
	/**
	 * Calculate the KL Divergence of two provided levels
	 * 
	 * @param level1 The first level to compare
	 * @param level2 The second level to compare
	 * @return A double representing the KL divergence
	 */
	private static double getKLDivergence(int[][] level1, int[][] level2) {
		ConvNTuple c1 = KLDivTest.getConvNTuple(level1, Parameters.parameters.integerParameter("receptiveFieldWidth"), Parameters.parameters.integerParameter("receptiveFieldHeight"), Parameters.parameters.integerParameter("stride"));
		ConvNTuple c2 = KLDivTest.getConvNTuple(level2, Parameters.parameters.integerParameter("receptiveFieldWidth"), Parameters.parameters.integerParameter("receptiveFieldHeight"), Parameters.parameters.integerParameter("stride"));

		double klDiv = KLDiv.klDivSymmetric(c1.sampleDis, c2.sampleDis);
		
		return klDiv;
	}

	@Override
	public String[] dimensions() {
		return new String[] {"First Level", "Second Level"};
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {klDivBinDimension, klDivBinDimension};
	}
	
	// test something here
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		MMNEAT.main(("runNumber:5 randomSeed:5 base:mariolevelskldivergence log:MarioLevelsKLDivergence-CMAME5Improvement saveTo:CMAME5Improvement marioGANLevelChunks:10 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:100 lambda:100 maxGens:100000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.CMAME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDecorNSAndLeniencyBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 mapElitesKLDivLevel1:data\\\\VGLC\\\\SuperMarioBrosNewEncoding\\\\overworld\\\\mario-8-1.txt mapElitesKLDivLevel2:data\\\\VGLC\\\\SuperMarioBrosNewEncoding\\\\overworld\\\\mario-3-1.txt klDivBinDimension:100 klDivMaxValue:0.3 numImprovementEmitters:5 numOptimizingEmitters:0").split(" "));
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		int[][] oneLevelAs2DArray = (int[][]) keys.get("2D Level");
		int[][][] klDivLevels = (int[][][]) keys.get("Comparison Levels");
		return discretize(behaviorCharacterization(oneLevelAs2DArray, klDivLevels));
	}	
}
