package edu.southwestern.tasks.evocraft.fitness;

import java.io.FileNotFoundException;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

/**
 * Counts number of pistons of either type (regular or sticky).
 * Extending TypeCountFitness only makes calculation of maxFitness easier.
 * Two internal instances of TypeCountFitness, one for each type.
 * 
 * @author Jacob Schrum
 *
 */
public class NumPistonsFitness extends TypeCountFitness {

	private TypeCountFitness piston;
	private TypeCountFitness stickyPiston;

	public NumPistonsFitness() {
		piston = new TypeCountFitness(BlockType.PISTON.ordinal());
		stickyPiston = new TypeCountFitness(BlockType.STICKY_PISTON.ordinal());
	}
	
	@Override
	public double fitnessScore(MinecraftCoordinates corner, List<Block> blocks) {
		//System.out.println(blocks);
		double total = piston.fitnessScore(corner,blocks);
		total += stickyPiston.fitnessScore(corner,blocks);
		return total;
	}
	//main for testing
	public static void main(String[] args) {
		try {
			MMNEAT.main("runNumber:1 randomSeed:1 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftTypeCountFitness:true minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:60000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:testing log:Testing-Testing saveTo:Testing mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBlockCountBinLabels arrayCrossover:edu.southwestern.evolution.crossover.ArrayCrossover".split(" ")); 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
