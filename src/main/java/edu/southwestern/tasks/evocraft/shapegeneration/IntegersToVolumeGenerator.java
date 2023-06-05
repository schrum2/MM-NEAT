package edu.southwestern.tasks.evocraft.shapegeneration;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;
import edu.southwestern.util.datastructures.ArrayUtil;

/**
 * 
 * @author raffertyt
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
			
				int maxIntGeneValue = MMNEAT.blockSet.getPossibleBlocks().length;
				
				upper = ArrayUtil.doubleSpecified(genotypeLength, maxIntGeneValue); // upper bounds generic genotype
				lower = ArrayUtil.doubleSpecified(genotypeLength, 0.0); // lower bounds generic genotype
	}
	/**
	 * 	 
	 * @param phenotype the ArrayList of blocks with associated with this shape, contains all attributes related to each block as numerical data in an array
	 */
	
	@Override
	public List<Block> generateShape(Genotype<ArrayList<Integer>> genome, MinecraftCoordinates corner,
			BlockSet blockSet) {
		//System.out.println("START");
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		//boolean evolveOrientation = Parameters.parameters.booleanParameter("minecraftEvolveOrientation");
		//boolean separatePresenceThreshold = Parameters.parameters.booleanParameter("vectorPresenceThresholdForEachBlock");
		//int numberOfAttributesPerBlock = 1;			// initial attribute is type
		//if(separatePresenceThreshold) numberOfAttributesPerBlock++;
		//if(evolveOrientation) numberOfAttributesPerBlock++;

		List<Block> blocks = new ArrayList<Block>();
		ArrayList<Integer> phenotype = genome.getPhenotype();	
		
//		System.out.println(phenotype);
//		System.out.println(phenotype.size());
		
		//Orientation blockOrientation = Orientation.NORTH; // all blocks will have orientation of north by default
		int blockHeadIndexCounter= 0; // used to count the number of blocks added, points to the beginning of the specific block attribute list in phenotype
		//int numBlockTypes = blockSet.getPossibleBlocks().length;
		//System.out.println("1 is running");
		for(int xi = 0; xi < ranges.x(); xi++) {
			for(int yi = 0; yi < ranges.y(); yi++) {
				for(int zi = 0; zi < ranges.z(); zi++) {		//loops through each block space to create a block
					Block b = null;
					//System.out.println("2 s running");
					// there will either be two or three numbers corresponding to one block's attributes/characteristics depending on if orientation is being evolved
					int blockTypeIndex = phenotype.get(blockHeadIndexCounter);
					//System.out.println(phenotype.get(blockHeadIndexCounter));
					//blockHeadIndexCounter+=numberOfAttributesPerBlock;	// increments to the next block head/start index in the phenotype
					b = new Block(corner.add(new MinecraftCoordinates(xi,yi,zi)), blockSet.getPossibleBlocks()[blockTypeIndex], MinecraftClient.Orientation.NORTH);
					blocks.add(b);
					blockHeadIndexCounter++;
				}
			}
		}
		assert blockHeadIndexCounter == phenotype.size() : "Counter "+blockHeadIndexCounter+" did not reach end of list: "+phenotype.size();
		//stem.out.println("3 s running");
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
		
		try {
			MMNEAT.main("runNumber:665 randomSeed:665 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.IntegersToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:60000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:false netio:false interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedIntegerValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5 crossover:edu.southwestern.evolution.crossover.ArrayCrossover".split(" ")); 

		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}


}

