package edu.southwestern.evolution.mapelites.generalmappings;

import java.io.FileNotFoundException;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;

public class LatentVariablePartitionSumBinLabels extends MultiDimensionalRealValuedSlicedBinLabels {

	/**
	 * Constructor, provides MultiDimensionalRealValuedSlicedBinLabels
	 * with several things:
	 * 
	 * binsPerDimension 	From parameter "latentPartitionBinDimension"
	 * minPossibleValue 	-1
	 * maxPossibleValue 	1 
	 * vectorLength 		From MMNEAT.getLowerBounds().length
	 */
	public LatentVariablePartitionSumBinLabels() {
		super(Parameters.parameters.integerParameter("latentPartitionBinDimension"), -1.0, 1.0, MMNEAT.getLowerBounds().length);
	}

	@Override
	protected double process(double value) {
		return value;
	}


	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// Mario
		//MMNEAT.main("runNumber:1 randomSeed:1 base:latentvariablepartition log:LatentVariablePartition-Mario saveTo:Mario marioGANLevelChunks:10 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:100 maxGens:100000 io:true netio:false genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.LatentVariablePartitionSumBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 solutionVectorSlices:2 latentPartitionBinDimension:100 ".split(" "));
		// Mario CMA-ME
		MMNEAT.main("runNumber:2 randomSeed:2 numImprovementEmitters:15 numOptimizingEmitters:0 base:latentvariablepartition log:LatentVariablePartition-MarioCMAME saveTo:MarioCMAME marioGANLevelChunks:10 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:100 maxGens:100000 io:true netio:false genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.CMAME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.LatentVariablePartitionSumBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 solutionVectorSlices:2 latentPartitionBinDimension:100 ".split(" "));
		// Zelda
		//MMNEAT.main("runNumber:0 randomSeed:1 zeldaCPPN2GANSparseKeys:true zeldaALlowPuzzleDoorUglyHack:false zeldaCPPNtoGANAllowsRaft:true zeldaCPPNtoGANAllowsPuzzleDoors:true zeldaDungeonBackTrackRoomFitness:true zeldaDungeonDistinctRoomFitness:true zeldaDungeonDistanceFitness:false zeldaDungeonFewRoomFitness:false zeldaDungeonTraversedRoomFitness:true zeldaPercentDungeonTraversedRoomFitness:true zeldaDungeonRandomFitness:false watch:false trials:1 mu:100 makeZeldaLevelsPlayable:false base:latentvariablepartition log:LatentVariablePartition-Zelda saveTo:Zelda zeldaGANLevelWidthChunks:5 zeldaGANLevelHeightChunks:5 zeldaGANModel:ZeldaDungeonsAll3Tiles_10000_10.pth maxGens:100000 io:true netio:true GANInputSize:10 mating:true fs:false task:edu.southwestern.tasks.zelda.ZeldaGANDungeonTask cleanOldNetworks:false zeldaGANUsesOriginalEncoding:false cleanFrequency:-1 saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.LatentVariablePartitionSumBinLabels steadyStateIndividualsPerGeneration:100 solutionVectorSlices:2 latentPartitionBinDimension:100 ".split(" "));
		// MegaMan
		//MMNEAT.main("runNumber:0 randomSeed:1 megaManAllowsConnectivity:false megaManAllowsSimpleAStarPath:true watch:false trials:1 mu:10 base:latentvariablepartition log:LatentVariablePartition-MegaMan saveTo:MegaMan megaManGANLevelChunks:10 maxGens:50000 io:true netio:true GANInputSize:5 mating:true fs:false task:edu.southwestern.tasks.megaman.MegaManGANLevelTask cleanOldNetworks:true useMultipleGANsMegaMan:true cleanFrequency:-1 recurrency:false saveAllChampions:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.LatentVariablePartitionSumBinLabels steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype solutionVectorSlices:2 latentPartitionBinDimension:100 ".split(" "));
		// Lode Runner
		//MMNEAT.main("runNumber:0 randomSeed:1 base:latentvariablepartition log:LatentVariablePartition-LodeRunner saveTo:LodeRunner LodeRunnerGANModel:LodeRunnerAllGround100LevelsEpoch200000_10_7.pth watch:false GANInputSize:10 trials:1 mu:100 maxGens:100000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.loderunner.LodeRunnerGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.LatentVariablePartitionSumBinLabels ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment allowWeirdLodeRunnerActions:false lodeRunnerMaximizeEnemies:false lodeRunnerTSPBudget:0 solutionVectorSlices:2 latentPartitionBinDimension:100 ".split(" "));
		// Mario 3 dims
		//MMNEAT.main("runNumber:0 randomSeed:1 base:latentvariablepartition log:LatentVariablePartition-MarioThree saveTo:MarioThree marioGANLevelChunks:10 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:100 maxGens:100000 io:true netio:false genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.LatentVariablePartitionSumBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 solutionVectorSlices:3 latentPartitionBinDimension:20 ".split(" "));
	}

}
