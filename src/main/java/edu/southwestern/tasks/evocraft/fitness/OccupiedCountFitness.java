package edu.southwestern.tasks.evocraft.fitness;

import java.io.FileNotFoundException;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

/**
 * Number of non-AIR blocks in the space
 * 
 * @author schrum2
 *
 */
public class OccupiedCountFitness extends CheckBlocksInSpaceFitness {

	@Override
	public double fitnessFromBlocks(MinecraftCoordinates corner, List<Block> blocks) {
		int total = 0;
		for(Block b : blocks) {
			if(b.type() != BlockType.AIR.ordinal()) {
				total++;
			}
		}
		
		return total;
	}

	@Override
	public double maxFitness() {
		// TODO: Might not be appropriate when evolving snakes
		return Parameters.parameters.integerParameter("minecraftXRange") * Parameters.parameters.integerParameter("minecraftYRange") * Parameters.parameters.integerParameter("minecraftZRange");
	}

	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		MMNEAT.main("runNumber:20 randomSeed:20 minecraftXRange:4 minecraftYRange:4 minecraftZRange:4 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:2 mu:100 maxGens:100000 minecraftContainsWholeMAPElitesArchive:true forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:7 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 base:minecraftaccumulate log:MinecraftAccumulate-VectorCount saveTo:VectorCount mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBlockCountBinLabels".split(" "));
	}
}
