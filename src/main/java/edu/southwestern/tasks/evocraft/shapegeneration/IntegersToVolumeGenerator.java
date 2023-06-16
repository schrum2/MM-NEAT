package edu.southwestern.tasks.evocraft.shapegeneration;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;

/**
 * New shape generator that makes use of integer based encoding.
 * @author raffertyt
 *
 */
public class IntegersToVolumeGenerator implements ShapeGenerator<ArrayList<Integer>> { //, BoundedVectorGenerator {

	private static final int PRESENCE_YES = 1;
	private static final int PRESENCE_NO = 0;

	private static int[] discreteCeilings = null;

	private static int genotypeLength = 0;
	private static int numOrientations = 0; // the number of orientations directions being considered for this instance (restricted or not)

	public IntegersToVolumeGenerator() {
		//range is the number of blocks that make up the shape
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();

		//characteristics (genes) represent block type, presence of block, and orientation

		boolean evolveOrientation = Parameters.parameters.booleanParameter("minecraftEvolveOrientation");
		boolean separatePresenceThreshold = Parameters.parameters.booleanParameter("vectorPresenceThresholdForEachBlock");
		int numberOfAttributesPerBlock = 1;			// initial attribute is type
		if(separatePresenceThreshold) numberOfAttributesPerBlock++;
		if(evolveOrientation) numberOfAttributesPerBlock++;

		//length of the genotype for the shape derived from the number of characteristics (genes) considered for blocks and the total volume in blocks used in the shape (numberOfAttributes * numberOfBlocksX * numberOfBlocksY * numberOfBlocksZ)
		genotypeLength = numberOfAttributesPerBlock * (ranges.x() * ranges.y() * ranges.z()); 

		numOrientations = MinecraftUtilClass.getnumOrientationDirections();	//orientations being considered for this shape's blocks (all 6 or just 2)			
		int numBlockTypes = MMNEAT.blockSet.getPossibleBlocks().length;
		int numPresenceValues = 2;

		System.out.println("Bounded genotype length will be: "+genotypeLength);
		discreteCeilings = new int[genotypeLength];
		for(int i = 0; i < genotypeLength; i += numberOfAttributesPerBlock) {
			int current = i;
			if(separatePresenceThreshold) {
				discreteCeilings[current++] = numPresenceValues;
			}
			discreteCeilings[current++] = numBlockTypes;
			if(evolveOrientation) {
				discreteCeilings[current++] = numOrientations;
			}
			assert i + numberOfAttributesPerBlock == current;
		}
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
		boolean evolveOrientation = Parameters.parameters.booleanParameter("minecraftEvolveOrientation");
		boolean separatePresenceThreshold = Parameters.parameters.booleanParameter("vectorPresenceThresholdForEachBlock");
		int numberOfAttributesPerBlock = 1;			// initial attribute is type
		if(separatePresenceThreshold) numberOfAttributesPerBlock++;
		if(evolveOrientation) numberOfAttributesPerBlock++;

		List<Block> blocks = new ArrayList<Block>();
		ArrayList<Integer> phenotype = genome.getPhenotype();	

		//		System.out.println(phenotype);
		//		System.out.println(phenotype.size());

		Orientation blockOrientation = Orientation.NORTH; // all blocks will have orientation of north by default
		int blockHeadIndexCounter = 0; // used to count the number of blocks added, points to the beginning of the specific block attribute list in phenotype

		for(int xi = 0; xi < ranges.x(); xi++) {
			for(int yi = 0; yi < ranges.y(); yi++) {
				for(int zi = 0; zi < ranges.z(); zi++) {		//loops through each block space to create a block
					Block b = null;

					if(separatePresenceThreshold) {

						final int PRESENCE_INDEX = blockHeadIndexCounter;
						final int TYPE_INDEX = blockHeadIndexCounter+1;
						
						assert PRESENCE_INDEX < phenotype.size() : ""+phenotype+" size "+phenotype.size()+"\nx="+xi+",y="+yi+",z="+zi+",blockHeadIndexCounter="+blockHeadIndexCounter+"\n"+Arrays.toString(discreteCeilings)+":"+genotypeLength+":numberOfAttributesPerBlock="+numberOfAttributesPerBlock;
						
						if(phenotype.get(PRESENCE_INDEX) == PRESENCE_YES) { // add a block
							int blockTypeIndex = phenotype.get(TYPE_INDEX);
							if(evolveOrientation) {
								final int ORIENTATION_INDEX = blockHeadIndexCounter+2;
								assert ORIENTATION_INDEX < phenotype.size() : ""+phenotype+" size "+phenotype.size()+"\nx="+xi+",y="+yi+",z="+zi+",blockHeadIndexCounter="+blockHeadIndexCounter+"\n"+Arrays.toString(discreteCeilings)+":"+genotypeLength+":numberOfAttributesPerBlock="+numberOfAttributesPerBlock;
								blockOrientation = MinecraftUtilClass.getOrientations()[phenotype.get(ORIENTATION_INDEX)];
							}
							b = new Block(corner.add(new MinecraftCoordinates(xi,yi,zi)), blockSet.getPossibleBlocks()[blockTypeIndex], blockOrientation);
						} else {
							// else do not add a block
							assert phenotype.get(PRESENCE_INDEX) == PRESENCE_NO : PRESENCE_INDEX+":"+phenotype+" size "+phenotype.size()+"\nx="+xi+",y="+yi+",z="+zi+",blockHeadIndexCounter="+blockHeadIndexCounter+"\n"+Arrays.toString(discreteCeilings)+":"+genotypeLength+":numberOfAttributesPerBlock="+numberOfAttributesPerBlock;
						}

					} else { // there will either be one or two numbers per block depending on if orientation is being evolved
						final int TYPE_INDEX = blockHeadIndexCounter;

						int blockTypeIndex = phenotype.get(TYPE_INDEX);
						if(evolveOrientation) {
							final int ORIENTATION_INDEX = blockHeadIndexCounter+1;
							blockOrientation = MinecraftUtilClass.getOrientations()[phenotype.get(ORIENTATION_INDEX)];
						}

						b = new Block(corner.add(new MinecraftCoordinates(xi,yi,zi)), blockSet.getPossibleBlocks()[blockTypeIndex], blockOrientation);
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

	@Override
	public String[] getNetworkOutputLabels() {
		throw new UnsupportedOperationException("This should not be called for vector-based shape generation");
	}

	public int[] getDiscreteCeilings() { return discreteCeilings; }

	//public double[] getLowerBounds() { return lower; }

	public static void main(String[] args) {

		try {
			//MMNEAT.main("runNumber:665 randomSeed:665 useWoxSerialization:false minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.DirectRepresentationShapeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:20 maxGens:3005 launchMinecraftServerFromJava:false io:true netio:true mating:true fs:false spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.tasks.evocraft.genotype.MinecraftShapeGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:minecraftaccumulate log:MinecraftAccumulate-ESObserverVectorTEST saveTo:ESObserverVectorTEST minecraftContainsWholeMAPElitesArchive:false rememberParentScores:true".split(" ")); 
			MMNEAT.main("runNumber:100 randomSeed:100 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.ExplosiveBlockSet trials:1 mu:100 maxGens:60000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:1 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:missileminecraft log:MissileMinecraft-MEObserverExplosiveVectorPistonOrientation saveTo:MEObserverExplosiveVectorPistonOrientation mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5 crossover:edu.southwestern.evolution.crossover.ArrayCrossover watch:true".split(" "));
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}


}

