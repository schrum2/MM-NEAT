package edu.southwestern.tasks.evocraft.fitness;

import java.io.FileNotFoundException;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.util.random.RandomNumbers;

/**
 * This fitness function is for testing and returns a random fitnessScore
 * @author lewisj
 *
 */
public class RandomFitness extends MinecraftFitnessFunction{
	@Override
	public double fitnessScore(MinecraftCoordinates corner, List<Block> originalBlocks) {
		return RandomNumbers.randomGenerator.nextDouble();
	}
	
	@Override
	public double maxFitness() {
		// TODO Auto-generated method stub
		return 1;
	}

	//main for testing
	public static void main(String[] args) {
		try {
			MMNEAT.main("runNumber:20 randomSeed:1 numVectorIndexMutations:1 polynomialMutation:false minecraftRandomFitness:true minecraftXRange:5 minecraftYRange:5 minecraftZRange:5 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.IntegersToVolumeGenerator minecraftChangeCenterOfMassFitness:false trials:1 mu:100 maxGens:100000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedIntegerValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:testing log:Testing-AirVSPistonInt saveTo:AirVSPistonInt mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesAirVSPistonBinLabels minecraftPistonLabelSize:5 spaceBetweenMinecraftShapes:15 minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet crossover:edu.southwestern.evolution.crossover.ArrayCrossover".split(" ")); 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
