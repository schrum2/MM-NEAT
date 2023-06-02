package edu.southwestern.tasks.evocraft.shapegeneration;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.BoundedTask;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;
import edu.southwestern.util.datastructures.ArrayUtil;

/**
 * This shape generator uses vectors to translate double values from an ArrayList into 
 * corresponding Minecraft block types. Depending on the command line parameters, there can 
 * be either one, two, or three numbers representing characteristics that a being considered that correspond to a single block. 
 * 
 * Creates a shape from a genotype
 * 
 * @author Alejandro Medina
 * Edited by Joanna Blatt Lewis
 *
 */
public class IntegersToVolumeGenerator implements ShapeGenerator<ArrayList<Integer>>, BoundedVectorGenerator {
	
	private static double[] upper = null;
	private static double[] lower = null;
	private static int genotypeLength = 0;
	//private static int numOrientations = 0; // the number of orientations directions being considered for this instance (restricted or not)
	
	public IntegersToVolumeGenerator() {
		//range is the number of blocks that make up the shape
				MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
				
				//characteristics (genes) represent block type, presence of block, and orientation
				
				int numberOfAttributesForBlocksInGenotype = 1; // represents block type, 1 is the lowest number of characteristics/attributes/genes corresponding to a block
				//if(Parameters.parameters.booleanParameter("minecraftEvolveOrientation")) numberOfAttributesForBlocksInGenotype++; // if considering orientation - set true and increase the number of characteristics (genes)
				//if(Parameters.parameters.booleanParameter("vectorPresenceThresholdForEachBlock")) numberOfAttributesForBlocksInGenotype++; // this creates gaps for air - if considering presence threshold (air or not) - set true and increase number of characteristics
				
				//length of the genotype for the shape derived from the number of characteristics (genes) considered for blocks and the total volume in blocks used in the shape (numberOfAttributes * numberOfBlocksX * numberOfBlocksY * numberOfBlocksZ)
				genotypeLength = numberOfAttributesForBlocksInGenotype * (ranges.x() * ranges.y() * ranges.z()); 
				//genotypeLength = 1+numberOfAttributesForBlocksInGenotype * (ranges.x() * ranges.y() * ranges.z()); 

				//numOrientations = MinecraftUtilClass.getnumOrientationDirections();	//orientations being considered for this shape's blocks (all 6 or just 2)
				
				upper = ArrayUtil.doubleSpecified(genotypeLength, 1.0); // upper bounds generic genotype
				lower = ArrayUtil.doubleSpecified(genotypeLength, 0.0); // lower bounds generic genotype
	}
	/**
	 * 	 
	 * @param phenotype the ArrayList of blocks with associated with this shape, contains all attributes related to each block as numerical data in an array
	 */
	
	@Override
	public List<Block> generateShape(Genotype<ArrayList<Integer>> genome, MinecraftCoordinates corner,
			BlockSet blockSet) {
		
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		//boolean evolveOrientation = Parameters.parameters.booleanParameter("minecraftEvolveOrientation");
		//boolean separatePresenceThreshold = Parameters.parameters.booleanParameter("vectorPresenceThresholdForEachBlock");
		//int numberOfAttributesPerBlock = 1;			// initial attribute is type
		//if(separatePresenceThreshold) numberOfAttributesPerBlock++;
		//if(evolveOrientation) numberOfAttributesPerBlock++;

		List<Block> blocks = new ArrayList<Block>();
		ArrayList<Integer> phenotype = genome.getPhenotype();	
		//Orientation blockOrientation = Orientation.NORTH; // all blocks will have orientation of north by default
		int blockHeadIndexCounter= 0; // used to count the number of blocks added, points to the beginning of the specific block attribute list in phenotype
		int numBlockTypes = blockSet.getPossibleBlocks().length;
		
		for(int xi = 0; xi < ranges.x(); xi++) {
			for(int yi = 0; yi < ranges.y(); yi++) {
				for(int zi = 0; zi < ranges.z(); zi++) {		//loops through each block space to create a block
					Block b = null;
					
					// there will either be two or three numbers corresponding to one block's attributes/characteristics depending on if orientation is being evolved
					int blockTypeIndex = (int)(phenotype.get(blockHeadIndexCounter)*(numBlockTypes)); // length+1 because there could be airblocks
					//blockHeadIndexCounter+=numberOfAttributesPerBlock;	// increments to the next block head/start index in the phenotype
					b = new Block(corner.add(new MinecraftCoordinates(xi,yi,zi)), blockSet.getPossibleBlocks()[blockTypeIndex], MinecraftClient.Orientation.NORTH);
					blocks.add(b);
					blockHeadIndexCounter++;
				}
			}
		}
		assert blockHeadIndexCounter == phenotype.size() : "Counter "+blockHeadIndexCounter+" did not reach end of list: "+phenotype.size();

		return blocks;	
	}

	
//	private Orientation determineOrientation(ArrayList<Integer> phenotype, final int ORIENTATION_INDEX) {
//		Orientation blockOrientation = null;
//		
//		return blockOrientation;
//	}

	@Override
	public String[] getNetworkOutputLabels() {
		throw new UnsupportedOperationException("This should not be called for vector-based shape generation");
	}
	
	public double[] getUpperBounds() { return upper; }

	public double[] getLowerBounds() { return lower; }
	
	public static void main(String[] args) {
		int seed = 0;
		try {
			MMNEAT.main(new String[] { "runNumber:" + seed, "randomSeed:" + seed, "trials:1", "mu:10", "maxGens:100000",
					"base:minecraft", "log:Minecraft-NorthSouthPistonCount", "saveTo:NorthSouthPistonCount",
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
					//"minecraftEvolveOrientation:false",
					
					
					
					//"oneOutputLabelForBlockTypeCPPN:true",
					//"oneOutputLabelForBlockOrientationCPPN:true",
					
					
					//"minecraftNorthSouthOnly:true",
					
					//"minecraftUpDownOnly:true",
					
					
					//"minecraftOccupiedCountFitness:true",
					//"minecraftEvolveOrientation:true",
					//"minecraftRedirectConfinedSnakes:true",
					//"minecraftStopConfinedSnakes:true",
					//"mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBlockCountBinLabels",
					
					"mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesNorthSouthPistonCountBinLabels",
					
					"ea:edu.southwestern.evolution.mapelites.MAPElites", 
					"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
					"steadyStateIndividualsPerGeneration:100",
					"minecraftXRange:2","minecraftYRange:2","minecraftZRange:2",
					"minecraftStopConfinedSnakes:true",
					"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator",
					"task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask", 
					"watch:false", "cleanFrequency:-1", "saveAllChampions:true"}); 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}


}

