package edu.southwestern.tasks.evocraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBinLabels;
import edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBlockCountBinLabels;
import edu.southwestern.tasks.evocraft.fitness.CheckBlocksInSpaceFitness;
import edu.southwestern.tasks.evocraft.fitness.OccupiedCountFitness;
import edu.southwestern.util.datastructures.Pair;

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
public class MinecraftLonerShapeTask<T> extends NoisyLonerTask<T> implements NetworkTask {

	private MinecraftShapeTask<T> internalMinecraftShapeTask;
	private static boolean spawnShapesInWorld=false;
	private static ArrayList<MinecraftCoordinates> parallelShapeCorners;
	
	private BlockingQueue<MinecraftCoordinates> coordinateQueue;

	public MinecraftLonerShapeTask() 	{
		/**
		 * Default shape generation location is shifted away a bit so that the archive can populate the world starting around (0,5,0) 
		 */
		
		internalMinecraftShapeTask = new MinecraftShapeTask<T>() {
			public int getStartingX() { return - getRanges().x() - Math.max(Parameters.parameters.integerParameter("minecraftMaxSnakeLength"), MinecraftClient.BUFFER); }

			public int getStartingZ() { return - getRanges().z() - Math.max(Parameters.parameters.integerParameter("minecraftMaxSnakeLength")*2, MinecraftClient.BUFFER); }
		};
		
		// Creates a new blocking queue to use with parallelism
		coordinateQueue = new ArrayBlockingQueue<>(Parameters.parameters.integerParameter("parallelMinecraftSlots"));
		
		// Generates the corners for all of the shapes and then adds them into the blocking queue
		parallelShapeCorners = MinecraftShapeTask.getShapeCorners(Parameters.parameters.integerParameter("parallelMinecraftSlots"),internalMinecraftShapeTask.getStartingX(),internalMinecraftShapeTask.getStartingZ(),internalMinecraftShapeTask.getRanges());
		for(int i =0;i<parallelShapeCorners.size();i++) {
			System.out.println(i);
			try {
				coordinateQueue.put(parallelShapeCorners.get(i));
			} catch (InterruptedException e) {
				System.out.println("Error with queue");
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num, HashMap<String, Object> behaviorCharacteristics) {
		MinecraftCoordinates ranges = internalMinecraftShapeTask.getRanges();

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
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(corner, ranges, 1, Math.max(Parameters.parameters.integerParameter("minecraftMaxSnakeLength"), MinecraftClient.BUFFER));
		
		// Evaluates the shape, and then adds the corner back to the queue
		Score<T> score = internalMinecraftShapeTask.evaluateOneShape(individual, corner);
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
	@SuppressWarnings("unchecked")
	public static <T> void placeArchiveInWorld(Genotype<T> individual, HashMap<String, Object> behaviorCharacteristics, MinecraftCoordinates ranges) {
		// Creates the bin labels
		MinecraftMAPElitesBinLabels minecraftBinLabels = (MinecraftMAPElitesBinLabels) MMNEAT.getArchiveBinLabelsClass();
		int index1D = (int) behaviorCharacteristics.get("dim1D");
		// Gets the bin scores to compare them
		double scoreOfCurrentElite = (double) behaviorCharacteristics.get("binScore");
		double scoreOfPreviousElite = ((MAPElites<T>) MMNEAT.ea).getArchive().getBinScore(index1D);

		// If the new shape is better than the previous, it gets replaced
		if(scoreOfCurrentElite > scoreOfPreviousElite) {
			int dimSize = minecraftBinLabels.dimensions().length;
			// Starting position is different for each dimension size, 2 and 3D use multidimensional, otherwise 1D
			Pair<MinecraftCoordinates,MinecraftCoordinates> corners = configureStartPosition(ranges, behaviorCharacteristics);
			if(!(Double.isInfinite(scoreOfPreviousElite) && scoreOfPreviousElite < 0)) {
				// Clears old shape, but only if a shape was there (score was not negative infinity)
				Pair<MinecraftCoordinates,MinecraftCoordinates> cleared = clearBlocksInArchive(dimSize, ranges, corners.t1);
				assert cleared.t1.equals(corners.t1) : "Cleared space does not start at right location: "+cleared.t1+" vs "+corners.t1;
				// Could do more checking here
			}
			// Generates the new shape
			List<Block> blocks = MMNEAT.shapeGenerator.generateShape(individual, corners.t2, MMNEAT.blockSet);
			
			// Spawning shapes is disabled during initialization
			if(spawnShapesInWorld) {
				MinecraftClient.getMinecraftClient().spawnBlocks(blocks);
				// Fences placed at initialization now
				//placeFencesAroundArchive(ranges,corners.t2);

				double testScore = 0;
				MinecraftCoordinates testCorner = null;
				assert MinecraftShapeTask.qualityScore(new double[] {testScore = ((MinecraftLonerShapeTask<T>) MMNEAT.task).internalMinecraftShapeTask.fitnessFunctions.get(0).fitnessScore(testCorner = configureStartPosition(ranges, behaviorCharacteristics).t2)}) == ((Double) behaviorCharacteristics.get("binScore")).doubleValue() : 
					behaviorCharacteristics + ":testScore="+testScore+":" + blocks;
				assert !(minecraftBinLabels instanceof MinecraftMAPElitesBlockCountBinLabels) || new OccupiedCountFitness().fitnessScore(testCorner) == (testScore = ((Double) behaviorCharacteristics.get("OccupiedCountFitness")).doubleValue()) : 
					testCorner+":occupied count="+testScore+":"+ blocks + ":" + CheckBlocksInSpaceFitness.readBlocksFromClient(testCorner);
			}
			
			if(CommonConstants.netio) {
				Archive<T> archive = MMNEAT.getArchive();
				String fileName = String.format("%7.5f", scoreOfCurrentElite) + "_" + individual.getId() + ".txt";
				int dim1D = (int) behaviorCharacteristics.get("dim1D");
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
		int dim1D = (int) behaviorCharacteristics.get("dim1D");
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
		final int SPACE_BETWEEN = Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes");
		MinecraftCoordinates startPosition;
		MinecraftCoordinates offset;
		int xOffset = (int) (((ranges.x() + SPACE_BETWEEN) / 2.0) - (ranges.x()/2.0)); 
		int yOffset = (int) (((ranges.y() + SPACE_BETWEEN) / 2.0) - (ranges.y()/2.0)); 
		int zOffset = (int) (((ranges.z() + SPACE_BETWEEN) / 2.0) - (ranges.z()/2.0)); 
		// Location in multi-dimensional archive
		if(multiDimIndex.length==1 || multiDimIndex.length > 3 || Parameters.parameters.booleanParameter("forceLinearArchiveLayoutInMinecraft")) {
			// Derive 1D location from multi-dimensional location
			startPosition = new MinecraftCoordinates(dim1D*(SPACE_BETWEEN+ranges.x()),MinecraftClient.GROUND_LEVEL+1,0);				
			offset = new MinecraftCoordinates(xOffset,0,0);				
		} else if(multiDimIndex.length==2){
			// Ground level fixed, but expand second coordinate in z dimension
			startPosition = new MinecraftCoordinates(multiDimIndex[0]*(SPACE_BETWEEN+ranges.x()),MinecraftClient.GROUND_LEVEL+1,multiDimIndex[1]*(SPACE_BETWEEN+ranges.z()));
			offset = new MinecraftCoordinates(xOffset,0,zOffset);
		} else if(multiDimIndex.length==3) {
			startPosition = new MinecraftCoordinates(multiDimIndex[0]*(SPACE_BETWEEN+ranges.x()),MinecraftClient.GROUND_LEVEL+1+multiDimIndex[1]*(SPACE_BETWEEN+ranges.y()),multiDimIndex[2]*(SPACE_BETWEEN+ranges.z()));
			offset = new MinecraftCoordinates(xOffset,yOffset,zOffset);
		} else {
			throw new IllegalArgumentException("This should be impossible to reach: "+Arrays.toString(multiDimIndex));
		}
		return new Pair<MinecraftCoordinates,MinecraftCoordinates>(startPosition, startPosition.add(offset));
	}

	/**
	 * Clears the an area for a specified shape 
	 * 
	 * @param dimSize Determines if the archive is 1D, 2D, or 3D
	 * @param ranges specified range of each shape
	 * @param startPosition Starting indices of a specific shape
	 * @return start and end coordinates of area that was cleared
	 */
	public static Pair<MinecraftCoordinates,MinecraftCoordinates> clearBlocksInArchive(int dimSize,MinecraftCoordinates ranges, MinecraftCoordinates startPosition) {
		final int SPACE_BETWEEN = Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes");
		// Makes the buffer space between coordinates
		MinecraftCoordinates bufferDist = new MinecraftCoordinates(SPACE_BETWEEN,SPACE_BETWEEN,SPACE_BETWEEN);
		// End coordinate is based on buffer distance. Then shape is cleared
		MinecraftCoordinates clearEnd = startPosition.add(bufferDist).add(ranges);
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
		fencePlacePosition=fencePlacePosition.sub(new MinecraftCoordinates(1,fencePlacePosition.y(),1));
		fencePlacePosition=fencePlacePosition.add(new MinecraftCoordinates(0,MinecraftClient.GROUND_LEVEL,0));
		
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
		int seed = 0;
		try {
			MMNEAT.main(new String[] { "runNumber:" + seed, "randomSeed:" + seed, "trials:1", "mu:100", "maxGens:100000",
					"base:minecraft", "log:Minecraft-MAPElitesWHDSimple", "saveTo:MAPElitesWHDSimple",
					"io:true", "netio:true",
					"minecraftContainsWholeMAPElitesArchive:true","forceLinearArchiveLayoutInMinecraft:false",
					"launchMinecraftServerFromJava:false",
					//"io:false", "netio:false", 
					"mating:true", "fs:false",
					"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.SimpleSolidBlockSet",
					//"minecraftTypeCountFitness:true",
					"minecraftDiversityBlockFitness:true",
					//"minecraftTypeTargetFitness:true", 
					//"minecraftDesiredBlockCount:40",
					//"minecraftOccupiedCountFitness:true",
					//"minecraftEvolveOrientation:true",
					"minecraftRedirectConfinedSnakes:true",
					//"minecraftStopConfinedSnakes:true", 
					"mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBlockCountBinLabels",
					"ea:edu.southwestern.evolution.mapelites.MAPElites", 
					"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
					"steadyStateIndividualsPerGeneration:100", 
					//FOR TESTING
					"spaceBetweenMinecraftShapes:10","parallelMAPElitesInitialize:true",
					"minecraftXRange:1","minecraftYRange:2","minecraftZRange:5",
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
}
