package edu.southwestern.tasks.evocraft.shapegeneration;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;
import edu.southwestern.util.datastructures.Pair;

/**
 * Shape generator for MinecraftShapeGenotype. Takes a hash map and turns it into an ArrayList.
 * @author raffertyt
 *
 */
public class DirectRepresentationShapeGenerator implements ShapeGenerator<Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>> {

	@Override
	public String[] getNetworkOutputLabels() {
		throw new UnsupportedOperationException("This should not be called for DirectRepresentationShapeGenerator");
	}

	@Override
	public List<Block> generateShape(
			Genotype<Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>> genome,
			MinecraftCoordinates corner, BlockSet blockSet) {
		Collection<Block> collectionOfBlocks =  genome.getPhenotype().t1.values();
		ArrayList<Block> blocks = new ArrayList<>(collectionOfBlocks);
		assert !MinecraftUtilClass.containsBlockType(blocks, BlockType.AIR) : "Generated shapes should not contain AIR: "+ blocks + "\n" + collectionOfBlocks + "\n" + genome.getPhenotype().t1;
		return MinecraftUtilClass.shiftBlocksBetweenCorners(blocks, new MinecraftCoordinates(0), corner);
	}
	
	public static void main(String[] args) {
		try {
			MMNEAT.main("runNumber:1 randomSeed:1 minecraftXRange:5 minecraftYRange:5 minecraftZRange:5 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.DirectRepresentationShapeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:100000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:14 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.tasks.evocraft.genotype.MinecraftShapeGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:directencoding log:DirectEncoding-BiggerVectorPistonOrientation saveTo:BiggerVectorPistonOrientation mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5 startY:40".split(" "));
		} catch (FileNotFoundException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
