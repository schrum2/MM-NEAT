package edu.southwestern.tasks.evocraft.fitness;

import java.io.FileNotFoundException;
import java.util.Arrays;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;

public class ChangeBlockAndChangeCenterOfMassWeightedFitness extends MinecraftWeightedSumFitnessFunction{

	public ChangeBlockAndChangeCenterOfMassWeightedFitness() {
		super(Arrays.asList(new MinecraftFitnessFunction[] {new ChangeBlocksFitness(), new ChangeCenterOfMassFitness()}),Arrays.asList(new Double[] {0.5,0.5}));
	}
	
	public static void main(String[] args) {
		try {
			//MMNEAT.main(("runNumber:100 randomSeed:99 numVectorIndexMutations:1 polynomialMutation:false minecraftXRange:5 minecraftYRange:5 minecraftZRange:5 minecraftRewardFastFlyingMachines:false minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.IntegersToVolumeGenerator minecraftChangeCenterOfMassFitness:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.RedstoneQuartzBlockSet trials:1 mu:100 maxGens:300000 minecraftContainsWholeMAPElitesArchive:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedIntegerValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:false parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:testing log:Testing-TESTING saveTo:TESTING mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesRedstoneVSQuartzBinLabels minecraftTypeCountFitness:true minecraftDesiredBlockType:"+BlockType.REDSTONE_BLOCK.ordinal()+" crossover:edu.southwestern.evolution.crossover.ArrayCrossover").split(" "));
			MMNEAT.main(("runNumber:2 randomSeed:1 minecraftChangeCenterOfMassFitness:false minecraftChangeBlockAndChangeCenterOfMassWeightedFitness:true minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:60000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:testing log:TESTING-WeightedSum saveTo:WeightedSum mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5 crossover:edu.southwestern.evolution.crossover.ArrayCrossover").split(" ")); 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
