package edu.southwestern.tasks.evocraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.MAPElites;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.BoundedTask;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.blocks.MachineBlockSet;
import edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBinLabels;
import edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBlockCountBinLabels;
import edu.southwestern.tasks.evocraft.fitness.CheckBlocksInSpaceFitness;
import edu.southwestern.tasks.evocraft.fitness.OccupiedCountFitness;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;

/**
 * MAPElites only works with LonerTasks because it is a steady-state algorithm.
 * MinecraftShapeTask is parallelized for evolving populations, but this
 * version of the task cannot take advantage of that (though there may be a work-around
 * using multiple emitters). However, some of the code from MinecraftShapeTask is
 * re-used.
 * 
 * @author schrum2
 *
 * @param <T>
 */
public class MinecraftLonerShapeTask<T> extends NoisyLonerTask<T> implements NetworkTask, BoundedTask {

	private MinecraftShapeTask<T> internalMinecraftShapeTask;
	private static boolean spawnShapesInWorld=false;
	private static ArrayList<MinecraftCoordinates> parallelShapeCorners;
	private BlockingQueue<MinecraftCoordinates> coordinateQueue;
	// Each diamond block refers to one shape, and the int is the associated 1D archive index
	private static Set<Triple<MinecraftCoordinates,MinecraftCoordinates,Integer>> blocksToMonitor = new HashSet<>();
	private static Thread interactionThread;

	public MinecraftLonerShapeTask() 	{
		/**
		 * Default shape generation location is shifted away a bit so that the archive can populate the world starting around (0,5,0) 
		 */

		internalMinecraftShapeTask = new MinecraftShapeTask<T>() {
			public int getStartingX() { return - Parameters.parameters.integerParameter("minecraftXRange") - Math.max(Parameters.parameters.integerParameter("minecraftMaxSnakeLength"), MinecraftClient.BUFFER); }

			public int getStartingZ() { return - Parameters.parameters.integerParameter("minecraftZRange") - Math.max(Parameters.parameters.integerParameter("minecraftMaxSnakeLength")*2, MinecraftClient.BUFFER); }
		};

		// Creates a new blocking queue to use with parallelism
		coordinateQueue = new ArrayBlockingQueue<>(Parameters.parameters.integerParameter("parallelMinecraftSlots"));

		// Generates the corners for all of the shapes and then adds them into the blocking queue
		parallelShapeCorners = MinecraftShapeTask.getShapeCorners(Parameters.parameters.integerParameter("parallelMinecraftSlots"),internalMinecraftShapeTask.getStartingX(),internalMinecraftShapeTask.getStartingZ(),MinecraftUtilClass.getRanges());
		for(MinecraftCoordinates corner : parallelShapeCorners) {
			try {
				coordinateQueue.put(corner);
			} catch (InterruptedException e) {
				System.out.println("Error with queue");
				e.printStackTrace();
				System.exit(1);
			}
		}

		if(Parameters.parameters.booleanParameter("interactWithMapElitesInWorld")) {
			blocksToMonitor = Collections.synchronizedSet(new HashSet<Triple<MinecraftCoordinates,MinecraftCoordinates,Integer>>());
			interactionThread = new Thread() {
				@Override
				public void run() {
					// Loop as long as evolution is running
					while(true) {
						@SuppressWarnings("unchecked")
						Triple<MinecraftCoordinates,MinecraftCoordinates,Integer>[] currentElements = new Triple[blocksToMonitor.size()];
						currentElements = blocksToMonitor.toArray(currentElements);

						// t1 is the diamond blocks, t2 is the emerald, and t3 is the 1D index
						for(Triple<MinecraftCoordinates,MinecraftCoordinates,Integer> pair : currentElements) {
							// Initial check
							if(MinecraftClient.getMinecraftClient().readCube(pair.t1).get(0).type!=BlockType.DIAMOND_BLOCK) {
								synchronized(blocksToMonitor) {
									// Verify that it is actually missing
									if(MinecraftClient.getMinecraftClient().readCube(pair.t1).get(0).type!=BlockType.DIAMOND_BLOCK) {
										System.out.println("--------------------------");
										System.out.println(MinecraftClient.getMinecraftClient().readCube(pair.t1,pair.t1)); // Fix, overloaded method
										@SuppressWarnings("unchecked")
										Score<T> s = MMNEAT.getArchive().getElite(pair.t3);

										placeArchiveInWorld(s.individual, s.MAPElitesBehaviorMap(), MinecraftUtilClass.getRanges(),true);
									}
								}
							}

						}
						
						try {
							Thread.sleep(Parameters.parameters.integerParameter("interactiveSleepTimer"));
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			};
			interactionThread.start();
		}
	}

	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num, HashMap<String, Object> behaviorCharacteristics) {
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();

		// Corner to clear and then place is taken from the queue. If the queue is empty, it waits until something is added in
		MinecraftCoordinates corner=null;
		try {
			corner = coordinateQueue.take();
		} catch (InterruptedException e) {
			System.out.println("Error with queue");
			e.printStackTrace();
			System.exit(1);
		}
		// Clears specified space for new shape
		clearBlocksInArchive(ranges, corner);
		MinecraftCoordinates middle = corner.add(MinecraftUtilClass.emptySpaceOffsets());
		// Evaluates the shape at the middle of the space defined by the corner, and then adds the corner back to the queue
		Score<T> score = internalMinecraftShapeTask.evaluateOneShape(individual, middle);
		try {
			coordinateQueue.put(corner);
		} catch (InterruptedException e) {
			System.out.println("Error with queue");
			e.printStackTrace();
			System.exit(1);
		}

		// Copy over one HashMap to another (is there an easier way?)
		for(HashMap.Entry<String,Object> entry : score.MAPElitesBehaviorMap().entrySet()) {
			behaviorCharacteristics.put(entry.getKey(), entry.getValue());
		}
		// Checks command line param on whether or not to generate shapes in archive
		if(Parameters.parameters.booleanParameter("minecraftContainsWholeMAPElitesArchive")) {
			// Places the shapes in the world based on their position
			placeArchiveInWorld(individual, behaviorCharacteristics, ranges);	
		}
		// This result will be ignored when using MAP Elites	
		return new Pair<>(score.scores, score.otherStats);
	}

	/**
	 * Generates the starting coordinates for spawning in the archived shapes. Then also checks if 
	 * the shapes should be added based on their score
	 * 
	 * @param individual specified genome of shape
	 * @param behaviorCharacteristics dictionary of values used for placing at the right index
	 * @param ranges specified range of each shape
	 */
	public static <T> void placeArchiveInWorld(Genotype<T> individual, HashMap<String, Object> behaviorCharacteristics, MinecraftCoordinates ranges) {
		placeArchiveInWorld(individual, behaviorCharacteristics, ranges, false); // By default, do not force placement of new shape
	}

	@SuppressWarnings("unchecked")
	public static <T> void placeArchiveInWorld(Genotype<T> individual, HashMap<String, Object> behaviorCharacteristics, MinecraftCoordinates ranges, boolean forcePlacement) {
		MinecraftMAPElitesBinLabels minecraftBinLabels = (MinecraftMAPElitesBinLabels) MMNEAT.getArchiveBinLabelsClass();
		// Don't try to place shapes that have no place to go
		if(!minecraftBinLabels.discard(behaviorCharacteristics)) {
			int index1D = minecraftBinLabels.oneDimensionalIndex(behaviorCharacteristics);
			// Gets the bin scores to compare them
			double scoreOfCurrentElite = (double) behaviorCharacteristics.get("binScore");
			assert index1D >= 0 : individual.getId() + ":" + behaviorCharacteristics;
			assert index1D < ((MAPElites<T>) MMNEAT.ea).getArchive().getBinMapping().binLabels().size() : individual.getId() + ":" + behaviorCharacteristics;
			double scoreOfPreviousElite = 0;
			scoreOfPreviousElite = ((MAPElites<T>) MMNEAT.ea).getArchive().getBinScore(index1D);

			// If the new shape is better than the previous, it gets replaced
			if(forcePlacement || scoreOfCurrentElite > scoreOfPreviousElite) {
				clearAndSpawnShape(individual, behaviorCharacteristics, ranges, index1D, scoreOfCurrentElite);
			}
		}
	}

	/**
	 * Clear a space for the shape and then generate it. Also, save block list to archive.
	 * 
	 * @param <T>
	 * @param individual specified genome of shape
	 * @param behaviorCharacteristics dictionary of values used for placing at the right index
	 * @param ranges bounds of generated shape
	 * @param index1D index in 1D archive
	 * @param scoreOfCurrentElite quality score
	 */
	@SuppressWarnings("unchecked")
	public static <T> void clearAndSpawnShape(Genotype<T> individual, HashMap<String, Object> behaviorCharacteristics,
			MinecraftCoordinates ranges, int index1D, double scoreOfCurrentElite) {
		
		List<Block> blocks = null;
		Pair<MinecraftCoordinates,MinecraftCoordinates> corners = configureStartPosition(ranges, behaviorCharacteristics);
		synchronized(blocksToMonitor) {

			// Clears old shape if there was one
			Pair<MinecraftCoordinates,MinecraftCoordinates> cleared = clearBlocksInArchive(ranges, corners.t1);
			assert cleared.t1.equals(corners.t1) : "Cleared space does not start at right location: "+cleared.t1+" vs "+corners.t1;
			// Could do more checking here

			// Generates the new shape
			blocks = MMNEAT.shapeGenerator.generateShape(individual, corners.t2, MMNEAT.blockSet);

			// Spawning shapes is disabled during initialization
			if(spawnShapesInWorld) {
				MinecraftClient.getMinecraftClient().spawnBlocks(blocks);
				if(Parameters.parameters.booleanParameter("interactWithMapElitesInWorld")) {
					List<Block> interactive = new ArrayList<>();
					MinecraftCoordinates diamondBlock = corners.t2.sub(new MinecraftCoordinates(1,1,1));
					interactive.add(new Block(diamondBlock,BlockType.DIAMOND_BLOCK, Orientation.WEST));
					MinecraftCoordinates emeraldBlock = corners.t2.add(new MinecraftCoordinates(MinecraftUtilClass.getRanges().x(),-1,-1));
					interactive.add(new Block(emeraldBlock,BlockType.EMERALD_BLOCK, Orientation.WEST));
					blocksToMonitor.add(new Triple<>(diamondBlock,emeraldBlock,index1D));
					MinecraftClient.getMinecraftClient().spawnBlocks(interactive);
				}

				// Fences placed at initialization now

				double testScore = 0;
				MinecraftCoordinates testCorner = null;
				assert !(((MinecraftLonerShapeTask<T>) MMNEAT.task).internalMinecraftShapeTask.fitnessFunctions.get(0) instanceof CheckBlocksInSpaceFitness && !(MMNEAT.blockSet instanceof MachineBlockSet)) || MinecraftShapeTask.qualityScore(new double[] {testScore = ((MinecraftLonerShapeTask<T>) MMNEAT.task).internalMinecraftShapeTask.fitnessFunctions.get(0).fitnessScore(testCorner = configureStartPosition(ranges, behaviorCharacteristics).t2)}) == ((Double) behaviorCharacteristics.get("binScore")).doubleValue() : 
					individual.getId() + ":" + testCorner + ":" + behaviorCharacteristics + ":testScore="+testScore+":\n" + ((MinecraftLonerShapeTask<T>) MMNEAT.task).internalMinecraftShapeTask.fitnessFunctions.get(0).getClass().getSimpleName() + ":\n" + blocks;
				assert !(MMNEAT.getArchiveBinLabelsClass() instanceof MinecraftMAPElitesBlockCountBinLabels) || new OccupiedCountFitness().fitnessScore(testCorner) == (testScore = ((Double) behaviorCharacteristics.get("OccupiedCountFitness")).doubleValue()) : 
					individual.getId() + ":" + testCorner+":occupied count="+testScore+":"+ blocks + ":" + CheckBlocksInSpaceFitness.readBlocksFromClient(testCorner);
			}
		}

		saveBlockListToMAPElitesArchive(individual.getId(), index1D, scoreOfCurrentElite, blocks);
	}

	/**
	 * Save a test list of the blocks in the generated shape to the archive directory for MAP Elites
	 * 
	 * @param <T>
	 * @param genomeId ID of genome being written
	 * @param dim1D location of genome in 1D archive
	 * @param scoreOfCurrentElite genome's score
	 * @param blocks blocks of the shape generated by genome
	 */
	public static <T> void saveBlockListToMAPElitesArchive(long genomeId, int dim1D, double scoreOfCurrentElite, List<Block> blocks) {
		if(CommonConstants.netio) {
			MinecraftMAPElitesBinLabels minecraftBinLabels = (MinecraftMAPElitesBinLabels) MMNEAT.getArchiveBinLabelsClass();
			@SuppressWarnings("unchecked")
			Archive<T> archive = MMNEAT.getArchive();
			String fileName = String.format("%7.5f", scoreOfCurrentElite) + "_" + genomeId + ".txt";
			String binPath = archive.getArchiveDirectory() + File.separator + minecraftBinLabels.binLabels().get(dim1D);
			String fullName = binPath + "_" + fileName;
			System.out.println(fullName);
			try {
				PrintStream outputFile = new PrintStream(new File(fullName));
				outputFile.println(blocks);
				outputFile.close();
			} catch (FileNotFoundException e) {
				System.out.println("Error writing file "+fullName);
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	/**
	 * Figure out the corner in the world to place the shape at in the representation of the archive.
	 * Both the corner of the large space that the shape reserves, and the actual corner of where it will be generated.
	 * 
	 * @param ranges x/y/z sizes taken up by each shape
	 * @param behaviorCharacteristics characteristics of the shape that help determine its location in the archive
	 * @return Pair of the corner of the space to clear followed by the corner within that space to place the shape
	 */
	public static Pair<MinecraftCoordinates,MinecraftCoordinates> configureStartPosition(MinecraftCoordinates ranges, HashMap<String,Object> behaviorCharacteristics) {
		MinecraftMAPElitesBinLabels minecraftBinLabels = (MinecraftMAPElitesBinLabels) MMNEAT.getArchiveBinLabelsClass();
		int[] multiDimIndex = minecraftBinLabels.multiDimensionalIndices(behaviorCharacteristics);
		int dim1D = minecraftBinLabels.oneDimensionalIndex(behaviorCharacteristics);
		return configureStartPosition(ranges, multiDimIndex, dim1D);
	}		

	/**
	 * Figure out the corner in the world to place the shape at in the representation of the archive.
	 * Both the corner of the large space that the shape reserves, and the actual corner of where it will be generated.

	 * @param ranges x/y/z sizes taken up by each shape
	 * @param multiDimIndex Multi-dimensional location in archive
	 * @param dim1D one-dimensional location in archive
	 * @return Pair of the corner of the space to clear followed by the corner within that space to place the shape
	 */
	public static Pair<MinecraftCoordinates,MinecraftCoordinates> configureStartPosition(MinecraftCoordinates ranges, int[] multiDimIndex, int dim1D) {
		MinecraftCoordinates startPosition;
		MinecraftCoordinates offset;
		int spaceBetween = Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes");
		// Location in multi-dimensional archive
		if(multiDimIndex.length==1 || multiDimIndex.length > 3 || Parameters.parameters.booleanParameter("forceLinearArchiveLayoutInMinecraft")) {
			// Derive 1D location from multi-dimensional location
			startPosition = new MinecraftCoordinates(dim1D*(spaceBetween+ranges.x()),MinecraftClient.GROUND_LEVEL+1,0);				
			offset = new MinecraftCoordinates(MinecraftUtilClass.emptySpaceOffsetX(),0,0);				
		} else if(multiDimIndex.length==2){
			// Ground level fixed, but expand second coordinate in z dimension
			startPosition = new MinecraftCoordinates(multiDimIndex[0]*(spaceBetween+ranges.x()),MinecraftClient.GROUND_LEVEL+1,multiDimIndex[1]*(spaceBetween+ranges.z()));
			offset = new MinecraftCoordinates(MinecraftUtilClass.emptySpaceOffsetX(),0,MinecraftUtilClass.emptySpaceOffsetZ());
		} else if(multiDimIndex.length==3) {
			startPosition = new MinecraftCoordinates(multiDimIndex[0]*(spaceBetween+ranges.x()),MinecraftClient.GROUND_LEVEL+1+multiDimIndex[1]*(spaceBetween+ranges.y()),multiDimIndex[2]*(spaceBetween+ranges.z()));
			offset = MinecraftUtilClass.emptySpaceOffsets();
		} else {
			throw new IllegalArgumentException("This should be impossible to reach: "+Arrays.toString(multiDimIndex));
		}
		return new Pair<MinecraftCoordinates,MinecraftCoordinates>(startPosition, startPosition.add(offset));
	}

	/**
	 * Clears the an area for a specified shape 
	 * 
	 * @param ranges specified range of each shape
	 * @param startPosition Starting indices of a specific shape
	 * @return start and end coordinates of area that was cleared
	 */
	public static Pair<MinecraftCoordinates,MinecraftCoordinates> clearBlocksInArchive(MinecraftCoordinates ranges, MinecraftCoordinates startPosition) {
		MinecraftCoordinates clearEnd = startPosition.add(MinecraftUtilClass.reservedSpace());
		// Sub 1 to not delete interactive blocks
		if(Parameters.parameters.booleanParameter("interactWithMapElitesInWorld")) clearEnd = clearEnd.sub(new MinecraftCoordinates(1)); 
		MinecraftClient.getMinecraftClient().fillCube(startPosition, clearEnd, BlockType.AIR);
		return new Pair<MinecraftCoordinates,MinecraftCoordinates>(startPosition, clearEnd);
	}

	/**
	 * Places fences around all specified shapes
	 * 
	 * @param ranges Size of blocks, used to generate fences
	 * @param fencePlacePosition Position where fences are added in from
	 */
	public static void placeFencesAroundArchive(MinecraftCoordinates ranges, MinecraftCoordinates fencePlacePosition) {
		List<Block> fences = new ArrayList<>();
		// Places first fences based on starting point
		fencePlacePosition=fencePlacePosition.sub(new MinecraftCoordinates(1,fencePlacePosition.y()-MinecraftClient.GROUND_LEVEL,1));

		// Places all fences in the x direction
		for(int i =0;i<=ranges.x()+1;i++) {
			fences.add(new Block(fencePlacePosition.x()+i,fencePlacePosition.y(),fencePlacePosition.z(),BlockType.DARK_OAK_FENCE, Orientation.WEST));
			fences.add(new Block(fencePlacePosition.x()+i,fencePlacePosition.y(),fencePlacePosition.z()+ranges.z()+1,BlockType.DARK_OAK_FENCE, Orientation.WEST));

		}
		// Places all fences in the z direction
		for(int i =0;i<=ranges.z();i++) {
			fences.add(new Block(fencePlacePosition.x(),fencePlacePosition.y(),fencePlacePosition.z()+i,BlockType.DARK_OAK_FENCE, Orientation.WEST));
			fences.add(new Block(fencePlacePosition.x()+ranges.x()+1,fencePlacePosition.y(),fencePlacePosition.z()+i,BlockType.DARK_OAK_FENCE, Orientation.WEST));
		}
		MinecraftClient.getMinecraftClient().spawnBlocks(fences); // Spawns them in
	}
	/**
	 * Sets spawnShapesInWorld to true
	 */
	public static void spawnShapesInWorldTrue() {
		spawnShapesInWorld=true;
	}

	/**
	 * Sets spawnShapesInWorld to false
	 */
	public static void spawnShapesInWorldFalse() {
		spawnShapesInWorld=false;
	}

	@Override
	public int numObjectives() {
		return internalMinecraftShapeTask.numObjectives();
	}

	@Override
	public double getTimeStamp() {
		return 0;
	}

	@Override
	public void postConstructionInitialization() {
		internalMinecraftShapeTask.postConstructionInitialization();
	}

	@Override
	public String[] sensorLabels() {
		return internalMinecraftShapeTask.sensorLabels();
	}

	@Override
	public String[] outputLabels() {
		return internalMinecraftShapeTask.outputLabels();
	}

	@Override
	public void finalCleanup() {
		internalMinecraftShapeTask.finalCleanup();
	}

	public static void main(String[] args) {
		int seed = 1;
		try {
			MMNEAT.main(new String[] { "runNumber:" + seed, "randomSeed:" + seed, "trials:1", "mu:100", "maxGens:100000",
					"base:minecraft", "log:Minecraft-MAPElitesWHDSimple", "saveTo:MAPElitesWHDSimple",
					"minecraftContainsWholeMAPElitesArchive:true","forceLinearArchiveLayoutInMinecraft:false",
					"launchMinecraftServerFromJava:false",
					"io:true", "netio:true",
					"interactWithMapElitesInWorld:true",
					//"io:false", "netio:false", 
					"mating:true", "fs:false",
					"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet",
					//"minecraftTypeCountFitness:true",
					"minecraftDiversityBlockFitness:true",
					//"minecraftTypeTargetFitness:true", 
					//"minecraftDesiredBlockCount:40",
					//"minecraftOccupiedCountFitness:true",
					//"minecraftEvolveOrientation:true",
					"minecraftRedirectConfinedSnakes:true",
					//"minecraftStopConfinedSnakes:true", 
					"mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesWidthHeightDepthBinLabels",
					"ea:edu.southwestern.evolution.mapelites.MAPElites", 
					"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
					"steadyStateIndividualsPerGeneration:100", 
					//FOR TESTING
					"spaceBetweenMinecraftShapes:3","parallelMAPElitesInitialize:false",
					"minecraftXRange:5","minecraftYRange:5","minecraftZRange:5",
					"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.ThreeDimensionalVolumeGenerator",
					"task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask", "allowMultipleFunctions:true",
					"ftype:0", "watch:false", "netChangeActivationRate:0.3", "cleanFrequency:-1",
					"recurrency:false", "saveAllChampions:true", "cleanOldNetworks:false",
					"includeFullSigmoidFunction:true", "includeFullGaussFunction:true", "includeCosineFunction:true", 
					"includeGaussFunction:false", "includeIdFunction:true", "includeTriangleWaveFunction:false", 
					"includeSquareWaveFunction:false", "includeFullSawtoothFunction:false", "includeSigmoidFunction:false", 
					"includeAbsValFunction:false", "includeSawtoothFunction:false"}); 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	@Override
	public double[] getUpperBounds() {
		return internalMinecraftShapeTask.getUpperBounds();
	}

	@Override
	public double[] getLowerBounds() {
		return internalMinecraftShapeTask.getLowerBounds();
	}
}
