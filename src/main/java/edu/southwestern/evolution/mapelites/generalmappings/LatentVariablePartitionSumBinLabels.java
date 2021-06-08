package edu.southwestern.evolution.mapelites.generalmappings;

import java.io.FileNotFoundException;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;

public class LatentVariablePartitionSumBinLabels extends MultiDimensionalRealValuedSlicedBinLabels {

	public LatentVariablePartitionSumBinLabels() {
		super(Parameters.parameters.integerParameter("latentPartitionBinDimension"), -1, 1, MMNEAT.getLowerBounds().length);
	}

	@Override
	protected double process(double value) {
		return value;
	}

	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		MMNEAT.main("runNumber:1 randomSeed:1 base:mariolevelsdecoratensleniency log:MarioLevelsDecorateNSLeniency-Direct2GAN saveTo:Direct2GAN marioGANLevelChunks:10 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:100 maxGens:100000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.LatentVariablePartitionSumBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 solutionVectorSlices:2 latentPartitionBinDimension:100 ".split(" "));
	}
}
