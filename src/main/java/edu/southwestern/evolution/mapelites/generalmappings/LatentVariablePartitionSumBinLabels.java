package edu.southwestern.evolution.mapelites.generalmappings;

import java.io.FileNotFoundException;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;

public class LatentVariablePartitionSumBinLabels extends MultiDimensionalRealValuedSlicedBinLabels {

	public LatentVariablePartitionSumBinLabels() {
		super(Parameters.parameters.integerParameter("latentPartitionBinDimension"), -1.0/Parameters.parameters.integerParameter("solutionVectorSlices"), 1.0/Parameters.parameters.integerParameter("solutionVectorSlices"), MMNEAT.getLowerBounds().length/Parameters.parameters.integerParameter("solutionVectorSlices"));
	}

	@Override
	protected double process(double value) {
		return value;
	}

	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// Mario
		//MMNEAT.main("runNumber:1 randomSeed:1 base:mariolevelsdecoratensleniency log:MarioLevelsDecorateNSLeniency-Direct2GAN saveTo:Direct2GAN marioGANLevelChunks:10 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:100 maxGens:100000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.LatentVariablePartitionSumBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 solutionVectorSlices:2 latentPartitionBinDimension:100 ".split(" "));
		// Zelda
		//MMNEAT.main("runNumber:1 randomSeed:1 zeldaCPPN2GANSparseKeys:true zeldaALlowPuzzleDoorUglyHack:false zeldaCPPNtoGANAllowsRaft:true zeldaCPPNtoGANAllowsPuzzleDoors:true zeldaDungeonBackTrackRoomFitness:true zeldaDungeonDistinctRoomFitness:true zeldaDungeonDistanceFitness:false zeldaDungeonFewRoomFitness:false zeldaDungeonTraversedRoomFitness:true zeldaPercentDungeonTraversedRoomFitness:true zeldaDungeonRandomFitness:false watch:false trials:1 mu:100 makeZeldaLevelsPlayable:false base:zeldadungeonsdistinctbtrooms log:ZeldaDungeonsDistinctBTRooms-Direct2GAN saveTo:Direct2GAN zeldaGANLevelWidthChunks:5 zeldaGANLevelHeightChunks:5 zeldaGANModel:ZeldaDungeonsAll3Tiles_10000_10.pth maxGens:100000 io:true netio:true GANInputSize:10 mating:true fs:false task:edu.southwestern.tasks.zelda.ZeldaGANDungeonTask cleanOldNetworks:false zeldaGANUsesOriginalEncoding:false cleanFrequency:-1 saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.LatentVariablePartitionSumBinLabels steadyStateIndividualsPerGeneration:100 solutionVectorSlices:2 latentPartitionBinDimension:100 ".split(" "));
		// MegaMan
		MMNEAT.main("runNumber:3 randomSeed:1 megaManAllowsConnectivity:false megaManAllowsSimpleAStarPath:true watch:false trials:1 mu:10 base:megamanmultigan log:MegaManMultiGAN-MegaManDirect2GAN saveTo:MegaManDirect2GAN megaManGANLevelChunks:10 maxGens:50000 io:true netio:true GANInputSize:5 mating:true fs:false task:edu.southwestern.tasks.megaman.MegaManGANLevelTask cleanOldNetworks:true useMultipleGANsMegaMan:true cleanFrequency:-1 recurrency:false saveAllChampions:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.LatentVariablePartitionSumBinLabels steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype solutionVectorSlices:2 latentPartitionBinDimension:100 ".split(" "));
		// Lode Runner
		//MMNEAT.main("runNumber:1 randomSeed:1 base:loderunnermapelites log:LodeRunnerMAPElites-ConnectedGroundLadders saveTo:ConnectedGroundLadders LodeRunnerGANModel:LodeRunnerAllGround100LevelsEpoch200000_10_7.pth watch:false GANInputSize:10 trials:1 mu:100 maxGens:100000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.loderunner.LodeRunnerGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.LatentVariablePartitionSumBinLabels ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment allowWeirdLodeRunnerActions:false lodeRunnerMaximizeEnemies:false lodeRunnerTSPBudget:0 solutionVectorSlices:2 latentPartitionBinDimension:100 ".split(" "));
	}
}
