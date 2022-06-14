package edu.southwestern.tasks.evocraft.shapegeneration;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;
import edu.southwestern.util.datastructures.ArrayUtil;

/**
 * This shape generator uses vectors to translate double values from an ArrayList into 
 * corresponding Minecraft block types. Depending on the command line parameters, there can 
 * be either one, two, or three numbers that correspond to a single block. 
 * 
 * @author Alejandro Medina
 *
 */
public class VectorToVolumeGenerator implements ShapeGenerator<ArrayList<Double>> {
	
	private static double[] upper = null;
	private static double[] lower = null;
	private static int numBlocks = 0;
	
	public VectorToVolumeGenerator() {
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		
		if(Parameters.parameters.booleanParameter("vectorPresenceThresholdForEachBlock")) numBlocks = 2*(ranges.x() * ranges.y() * ranges.z()); // two numbers per block
		else numBlocks = ranges.x() * ranges.y() * ranges.z(); // one number per block

		upper = ArrayUtil.doubleSpecified(numBlocks, 1.0); // upper bounds
		lower = ArrayUtil.doubleSpecified(numBlocks, 0.0); // lower bounds
	}
	
	@Override
	public List<Block> generateShape(Genotype<ArrayList<Double>> genome, MinecraftCoordinates corner,
			BlockSet blockSet) {
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		
		List<Block> blocks = new ArrayList<Block>();
		ArrayList<Double> phenotype = genome.getPhenotype();	
		Orientation blockOrientation = Orientation.NORTH; // all blocks will have orientation of north for now
		int counter= 0; // used to count the number of blocks added
		int numBlockTypes = blockSet.getPossibleBlocks().length;
		for(int xi = 0; xi < ranges.x(); xi++) {
			for(int yi = 0; yi < ranges.y(); yi++) {
				for(int zi = 0; zi < ranges.z(); zi++) {
					//System.out.println(phenotype);
					Block b = null;
					
					// Increment by 2 instead of one since two numbers correspond to 1 block
					if(Parameters.parameters.booleanParameter("vectorPresenceThresholdForEachBlock")) {
						final int PRESENCE_INDEX = counter;
						final int TYPE_INDEX = counter+1;
						if(phenotype.get(PRESENCE_INDEX) >= Parameters.parameters.doubleParameter("voxelExpressionThreshold")) {
							int blockTypeIndex = (int)(phenotype.get(TYPE_INDEX)*numBlockTypes); // length because there are no AIR blocks in list for this case (presence takes care of this)		
							if(blockTypeIndex == numBlockTypes) blockTypeIndex--; // Rare case
							b = new Block(corner.add(new MinecraftCoordinates(xi,yi,zi)), blockSet.getPossibleBlocks()[blockTypeIndex], blockOrientation);
						} // else do not place any block
						counter+=2; // two numbers per block
					} else { 
						int blockTypeIndex = (int)(phenotype.get(counter)*(numBlockTypes+1)); // length+1 because there could be airblocks
						if(blockTypeIndex != numBlockTypes) { // Corresponds to empty/AIR block
							b = new Block(corner.add(new MinecraftCoordinates(xi,yi,zi)), blockSet.getPossibleBlocks()[blockTypeIndex], blockOrientation);
						}
						counter++; // one number per block
					}
					// Will be null if empty/AIR
					if(b!=null) blocks.add(b);
				}
			}
		}
		return blocks;	
	}

	@Override
	public String[] getNetworkOutputLabels() {
		return ShapeGenerator.defaultNetworkOutputLabels(MMNEAT.blockSet);
	}
	
	public double[] getUpperBounds() { return upper; }

	public double[] getLowerBounds() { return lower; }
	
	public static void main(String[] args) {
		int seed = 0;
		try {
			MMNEAT.main(new String[] { "runNumber:" + seed, "randomSeed:" + seed, "trials:1", "mu:100", "maxGens:100000",
					"base:minecraft", "log:Minecraft-VectorToVolumeTest", "saveTo:VectorToVolumeTest",
					"io:true", "netio:true", 
					"launchMinecraftServerFromJava:false",
					//"io:false", "netio:false", 
					"mating:true", "fs:false", 
					"minecraftContainsWholeMAPElitesArchive:true",
					"genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype",
					//"minecraftTypeCountFitness:true",
					//"minecraftDiversityBlockFitness:true",
					"minecraftChangeCenterOfMassFitness:true",
					//"minecraftTypeTargetFitness:true", 
					//"minecraftDesiredBlockCount:40",
					"minecraftContainsWholeMAPElitesArchive:true",
					"spaceBetweenMinecraftShapes:10","parallelMAPElitesInitialize:true",
					
					
					
					
					"vectorPresenceThresholdForEachBlock:true",
					"voxelExpressionThreshold:0.5",
					
					
					
					
					//"minecraftOccupiedCountFitness:true",
					//"minecraftEvolveOrientation:true",
					//"minecraftRedirectConfinedSnakes:true",
					//"minecraftStopConfinedSnakes:true",
					"mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBlockCountBinLabels",
					"ea:edu.southwestern.evolution.mapelites.MAPElites", 
					"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
					"steadyStateIndividualsPerGeneration:100",
					"minecraftXRange:3","minecraftYRange:3","minecraftZRange:5",
					"minecraftStopConfinedSnakes:true",
					"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator",
					"task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask", 
					"watch:false", "cleanFrequency:-1", "saveAllChampions:true"}); 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
