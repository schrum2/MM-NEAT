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
public class VectorToVolumeGenerator implements ShapeGenerator<ArrayList<Double>>, BoundedVectorGenerator {
	
	private static double[] upper = null;
	private static double[] lower = null;
	private static int genotypeLength = 0;
	private static int numOrientations = 0;
	
	/**
	 * This basic constructor creates the genotype length for the given shape and the basic upper and lower bounds of the genotype.
	 * It sets the number of orientations being considered.
	 */
	public VectorToVolumeGenerator() {
		//range is the number of blocks that make up the shape
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		
		//characteristics (genes) represent block type, presence of block, and orientation
		
		int numberOfAttributesForBlocksInGenotype = 1; // 1 is the lowest number of characteristics/attributes/genes corresponding to a block
		if(Parameters.parameters.booleanParameter("minecraftEvolveOrientation")) numberOfAttributesForBlocksInGenotype++; // evolve orientation is true, number of corresponding characteristics (genes) per block should be increased by 1
		if(Parameters.parameters.booleanParameter("vectorPresenceThresholdForEachBlock")) numberOfAttributesForBlocksInGenotype++; // presence is true, number of corresponding characteristics (genes) per block should be increased by 1 
		
		//derived from given parameters
		//length of the genotype for the shape derived from the number of characteristics (genes) considered for blocks and the total volume in blocks used in the shape (numberOfAttributes * numberOfBlocksX * numberOfBlocksY * numberOfBlocksZ)
		genotypeLength = numberOfAttributesForBlocksInGenotype * (ranges.x() * ranges.y() * ranges.z()); // one or more numbers per block depending on command line parameters
		numOrientations = MinecraftUtilClass.getnumOrientationDirections();	//orientations being considered for this instance
		
		upper = ArrayUtil.doubleSpecified(genotypeLength, 1.0); // upper bounds generic genotype
		lower = ArrayUtil.doubleSpecified(genotypeLength, 0.0); // lower bounds generic genotype
	}
	
	@Override
	public List<Block> generateShape(Genotype<ArrayList<Double>> genome, MinecraftCoordinates corner,
			BlockSet blockSet) {
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		boolean evolveOrientation = Parameters.parameters.booleanParameter("minecraftEvolveOrientation");
		boolean separatePresenceThreshold = Parameters.parameters.booleanParameter("vectorPresenceThresholdForEachBlock");
		int numberOfAttributesPerBlock = 1;
		if(separatePresenceThreshold) numberOfAttributesPerBlock++;
		if(evolveOrientation) numberOfAttributesPerBlock++;

		List<Block> blocks = new ArrayList<Block>();
		ArrayList<Double> phenotype = genome.getPhenotype();	
		Orientation blockOrientation = Orientation.NORTH; // all blocks will have orientation of north by default
		int blockHeadIndexCounter= 0; // used to count the number of blocks added, points to the beginning of the specific block attribute list in phenotype
		int numBlockTypes = blockSet.getPossibleBlocks().length;
		for(int xi = 0; xi < ranges.x(); xi++) {
			for(int yi = 0; yi < ranges.y(); yi++) {
				for(int zi = 0; zi < ranges.z(); zi++) {		//loops through each block space to create a block
					Block b = null;
					
					// there will either be two or three numbers corresponding to one block's attributes/characteristics depending on if orientation is being evolved
					if(separatePresenceThreshold) {
						
						final int PRESENCE_INDEX = blockHeadIndexCounter;
						final int TYPE_INDEX = blockHeadIndexCounter+1;
						
						assert phenotype.get(PRESENCE_INDEX) >= ((BoundedTask) MMNEAT.task).getLowerBounds()[PRESENCE_INDEX] : PRESENCE_INDEX+":bounds="+((BoundedTask) MMNEAT.task).getLowerBounds()+":phenotype="+phenotype;
						assert phenotype.get(PRESENCE_INDEX) <= ((BoundedTask) MMNEAT.task).getUpperBounds()[PRESENCE_INDEX] : PRESENCE_INDEX+":bounds="+((BoundedTask) MMNEAT.task).getUpperBounds()+":phenotype="+phenotype;
						assert phenotype.get(TYPE_INDEX) >= ((BoundedTask) MMNEAT.task).getLowerBounds()[TYPE_INDEX] : (TYPE_INDEX)+":bounds="+((BoundedTask) MMNEAT.task).getLowerBounds()+":phenotype="+phenotype;
						assert phenotype.get(TYPE_INDEX) <= ((BoundedTask) MMNEAT.task).getUpperBounds()[TYPE_INDEX] : (TYPE_INDEX)+":bounds="+((BoundedTask) MMNEAT.task).getUpperBounds()+":phenotype="+phenotype;
						
						if(phenotype.get(PRESENCE_INDEX) >= Parameters.parameters.doubleParameter("voxelExpressionThreshold")) {
							assert phenotype.get(TYPE_INDEX) >= 0 : "index:"+TYPE_INDEX+":"+phenotype;
							int blockTypeIndex = (int)(phenotype.get(TYPE_INDEX)*numBlockTypes); // length because there are no AIR blocks in list for this case (presence takes care of this)		
							assert blockTypeIndex >= 0.0 : "Index "+TYPE_INDEX+ " of " + phenotype + " multiplied by " + numBlockTypes;

							if(blockTypeIndex == numBlockTypes) blockTypeIndex--; // Rare case
							
							if(evolveOrientation) {
								final int ORIENTATION_INDEX = blockHeadIndexCounter+2;

								assert phenotype.get(ORIENTATION_INDEX) >= ((BoundedTask) MMNEAT.task).getLowerBounds()[ORIENTATION_INDEX] : (ORIENTATION_INDEX)+":bounds="+((BoundedTask) MMNEAT.task).getLowerBounds()+":phenotype="+phenotype;
								assert phenotype.get(ORIENTATION_INDEX) <= ((BoundedTask) MMNEAT.task).getUpperBounds()[ORIENTATION_INDEX] : (ORIENTATION_INDEX)+":bounds="+((BoundedTask) MMNEAT.task).getUpperBounds()+":phenotype="+phenotype;
										
								blockOrientation = determineOrientation(phenotype, ORIENTATION_INDEX);
							}
								
							b = new Block(corner.add(new MinecraftCoordinates(xi,yi,zi)), blockSet.getPossibleBlocks()[blockTypeIndex], blockOrientation);
						} // else do not place any block
					} else { // there will either be one or two numbers per block depending on if orientation is being evolved
						int blockTypeIndex = (int)(phenotype.get(blockHeadIndexCounter)*(numBlockTypes+1)); // length+1 because there could be airblocks
						if(blockTypeIndex != numBlockTypes) { // Corresponds to empty/AIR block
							
							if(evolveOrientation) {
								final int ORIENTATION_INDEX = blockHeadIndexCounter+1;
								blockOrientation = determineOrientation(phenotype, ORIENTATION_INDEX);
							}
							
							b = new Block(corner.add(new MinecraftCoordinates(xi,yi,zi)), blockSet.getPossibleBlocks()[blockTypeIndex], blockOrientation);
						}
					}
					// Will be null if empty/AIR
					if(b!=null) blocks.add(b);
					blockHeadIndexCounter+=numberOfAttributesPerBlock;	// increments to the next block head/start index in the phenotype
				}
			}
		}
		
		assert blockHeadIndexCounter == phenotype.size() : "Counter "+blockHeadIndexCounter+" did not reach end of list: "+phenotype.size();
		
		return blocks;	
	}

	/**
	 * 
	 * @param phenotype
	 * @param ORIENTATION_INDEX
	 * @return
	 */
	private Orientation determineOrientation(ArrayList<Double> phenotype, final int ORIENTATION_INDEX) {
		Orientation blockOrientation;
		int orientationTypeIndex = (int) (phenotype.get(ORIENTATION_INDEX) * numOrientations);
		if(orientationTypeIndex == numOrientations) {
			assert phenotype.get(ORIENTATION_INDEX) == 1.0;
			orientationTypeIndex--; // Boundary case
		}
		blockOrientation = MinecraftUtilClass.getOrientations()[orientationTypeIndex];
		return blockOrientation;
	}

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
