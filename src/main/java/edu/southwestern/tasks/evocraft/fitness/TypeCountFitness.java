package edu.southwestern.tasks.evocraft.fitness;

import java.io.FileNotFoundException;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

/**
 * Fitness score is the number of occurrences of a block of a specific type within the bounds of the generated shape.
 * @author schrum2
 *
 */
public class TypeCountFitness extends MinecraftFitnessFunction {
	
	private int desiredType;
	
	public TypeCountFitness(int type) {
		desiredType = type;
	}
	
	public TypeCountFitness() {
		desiredType = Parameters.parameters.integerParameter("minecraftDesiredBlockType");
	}
	
	@Override
	public double fitnessScore(MinecraftCoordinates corner, List<Block> blocks) {
		return typeCount(blocks,desiredType);
	}

	public static double typeCount(List<Block> blocks, int desiredType) {
		int total = 0;
		for(Block b : blocks) {
			if(b.type() == desiredType) {
				total++;
			}
		}
		
		return total;
	}

	@Override
	public double maxFitness() {
		return Parameters.parameters.integerParameter("minecraftXRange") * Parameters.parameters.integerParameter("minecraftYRange") * Parameters.parameters.integerParameter("minecraftZRange");
	}
	
	public static void main(String[] args) {
		try {
			MMNEAT.main(("runNumber:90 randomSeed:98 minecraftXRange:5 minecraftYRange:5 minecraftZRange:5 minecraftRewardFastFlyingMachines:false minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.RedstoneQuartzBlockSet trials:1 mu:100 maxGens:200000 minecraftContainsWholeMAPElitesArchive:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:false parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:testing log:Testing-TESTING saveTo:TESTING mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesRedstoneVSQuartzBinLabels minecraftTypeCountFitness:true minecraftDesiredBlockType:"+BlockType.REDSTONE_BLOCK.ordinal()+" crossover:edu.southwestern.evolution.crossover.real.SBX").split(" ")); 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
