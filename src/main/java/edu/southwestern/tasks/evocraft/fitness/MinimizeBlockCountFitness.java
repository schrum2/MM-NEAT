package edu.southwestern.tasks.evocraft.fitness;

import java.io.FileNotFoundException;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

/**
 * created to test hypervolume using negative fitness.
 * Encourages negative fitness score.
 * @author lewisj
 *
 */
public class MinimizeBlockCountFitness extends MinecraftFitnessFunction {

	private int unDesiredType;
	
	public MinimizeBlockCountFitness() {
		unDesiredType = Parameters.parameters.integerParameter("minecraftDesiredBlockType");
	}
	
	@Override
	public double fitnessScore(MinecraftCoordinates corner, List<Block> blocks) {
		return typeCount(blocks,unDesiredType);
	}

	public static double typeCount(List<Block> blocks, int desiredType) {
		int total = 0;
		for(Block b : blocks) {
			if(b.type() == desiredType) {
				total--;
			}
		}
		
		return total;
	}

	@Override
	public double minFitness() {
		return -(Parameters.parameters.integerParameter("minecraftXRange") * Parameters.parameters.integerParameter("minecraftYRange") * Parameters.parameters.integerParameter("minecraftZRange"));
	}
	@Override
	public double maxFitness() {
		return 0;
	}
	
	public static void main(String[] args) {
		try {
			//MMNEAT.main(("runNumber:100 randomSeed:99 numVectorIndexMutations:1 polynomialMutation:false minecraftXRange:5 minecraftYRange:5 minecraftZRange:5 minecraftRewardFastFlyingMachines:false minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.IntegersToVolumeGenerator minecraftChangeCenterOfMassFitness:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.RedstoneQuartzBlockSet trials:1 mu:100 maxGens:300000 minecraftContainsWholeMAPElitesArchive:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedIntegerValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:false parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:testing log:Testing-TESTING saveTo:TESTING mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesRedstoneVSQuartzBinLabels minecraftTypeCountFitness:true minecraftDesiredBlockType:"+BlockType.REDSTONE_BLOCK.ordinal()+" arrayCrossover:edu.southwestern.evolution.crossover.ArrayCrossover").split(" "));
			//MMNEAT.main(("runNumber:1 randomSeed:99 numVectorIndexMutations:1 polynomialMutation:false minecraftXRange:5 minecraftYRange:5 minecraftZRange:5 minecraftRewardFastFlyingMachines:false minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.IntegersToVolumeGenerator minecraftChangeCenterOfMassFitness:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.RedstoneQuartzBlockSet trials:1 mu:10 maxGens:1 minecraftContainsWholeMAPElitesArchive:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:10 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedIntegerValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:false parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:testing log:Testing-MinBlockCount saveTo:MinBlockCount mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesRedstoneVSQuartzBinLabels minecraftTypeCountFitness:false minecraftDesiredBlockType:"+BlockType.REDSTONE_BLOCK.ordinal()+" arrayCrossover:edu.southwestern.evolution.crossover.ArrayCrossover").split(" ")); 
			MMNEAT.main(("runNumber:2 randomSeed:99 minecraftMinimizeBlockCountFitness:true saveWholeMinecraftArchiveAtEnd:false minecraftOccupiedCountFitness:true maximumMOMESubPopulationSize:5 numVectorIndexMutations:1 polynomialMutation:false minecraftXRange:5 minecraftYRange:5 minecraftZRange:5 minecraftRewardFastFlyingMachines:false minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.IntegersToVolumeGenerator minecraftChangeCenterOfMassFitness:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.RedstoneQuartzBlockSet trials:1 mu:10 maxGens:15 minecraftContainsWholeMAPElitesArchive:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mome.MOME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedIntegerValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:false parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:testing log:Testing-MinBlockCount saveTo:MinBlockCount mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesRedstoneVSQuartzBinLabels minecraftDesiredBlockType:"+BlockType.REDSTONE_BLOCK.ordinal()+" arrayCrossover:edu.southwestern.evolution.crossover.ArrayCrossover").split(" ")); 

		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}

