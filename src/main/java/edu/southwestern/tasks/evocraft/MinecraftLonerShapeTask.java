package edu.southwestern.tasks.evocraft;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.MAPElites;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBinLabels;
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

	public MinecraftLonerShapeTask() {
		/**
		 * Default shape generation location is shifted away a bit so that the archive can populate the world starting around (0,5,0) 
		 */
		internalMinecraftShapeTask = new MinecraftShapeTask<T>() {
			public int getStartingX() { return - getRanges().x() - Math.max(Parameters.parameters.integerParameter("minecraftMaxSnakeLength"), MinecraftClient.BUFFER); }

			public int getStartingZ() { return - getRanges().z() - Math.max(Parameters.parameters.integerParameter("minecraftMaxSnakeLength"), MinecraftClient.BUFFER); }

		};
	}

	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num, HashMap<String, Object> behaviorCharacteristics) {
		// It would be good to change the evaluation scheme so this is not true
		assert !Parameters.parameters.booleanParameter("parallelMAPElitesInitialize") : "Since all shapes are evaluated at the same location, they cannot be evaluated in parallel";

		int startingX = internalMinecraftShapeTask.getStartingX();
		int startingZ = internalMinecraftShapeTask.getStartingZ();
		MinecraftCoordinates ranges = internalMinecraftShapeTask.getRanges();
		// Clears space for one shape
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(new MinecraftCoordinates(startingX,MinecraftClient.GROUND_LEVEL+1,startingZ), ranges, 1, Math.max(Parameters.parameters.integerParameter("minecraftMaxSnakeLength"), MinecraftClient.BUFFER));
		// List of 1 corner
		ArrayList<MinecraftCoordinates> corner = MinecraftShapeTask.getShapeCorners(1, startingX, startingZ, ranges);

		Score<T> score = internalMinecraftShapeTask.evaluateOneShape(individual, corner.get(0));
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
		// Creates the bin labels
		MinecraftMAPElitesBinLabels minecraftBinLabels = (MinecraftMAPElitesBinLabels) MMNEAT.getArchiveBinLabelsClass();
		int dimSize = minecraftBinLabels.dimensions().length;
		int index1D = (int) behaviorCharacteristics.get("dim1D");
		// Starting position is different for each dimension size, 2 and 3D use multidimensional, otherwise 1D
		Pair<MinecraftCoordinates,MinecraftCoordinates> corners = configureStartPosition(ranges, behaviorCharacteristics);

		// Gets the bin scores to compare them
		double scoreOfCurrentElite = (double) behaviorCharacteristics.get("binScore");
		@SuppressWarnings("unchecked")
		double scoreOfPreviousElite = ((MAPElites<T>) MMNEAT.ea).getArchive().getBinScore(index1D);

		// If the new shape is better than the previous, it gets replaced
		if(scoreOfCurrentElite > scoreOfPreviousElite) {
			if(!(Double.isInfinite(scoreOfPreviousElite) && scoreOfPreviousElite < 0)) {
				// Clears old shape, but only if a shape was there (score was not negative infinity)
				Pair<MinecraftCoordinates,MinecraftCoordinates> cleared = clearBlocksInArchive(dimSize, ranges, corners.t1);
				assert cleared.t1.equals(corners.t1) : "Cleared space does not start at right location: "+cleared.t1+" vs "+corners.t1;
				// Could do more checking here
			}
			// Generates the new shape
			@SuppressWarnings("unchecked")
			List<Block> blocks = MMNEAT.shapeGenerator.generateShape(individual, corners.t2, MMNEAT.blockSet);
			MinecraftClient.getMinecraftClient().spawnBlocks(blocks);
		}
	}

	/**
	 * Figure out the corner in the world to place the shape at in the representation of the archive.
	 * 
	 * @param ranges x/y/z sizes taken up by each shape
	 * @param behaviorCharacteristics characteristics of the shape that help determine its location in the archive
	 * @return Pair of the corner of the space to clear followed by the corner within that space to place the shape
	 */
	public static Pair<MinecraftCoordinates,MinecraftCoordinates> configureStartPosition(MinecraftCoordinates ranges, HashMap<String,Object> behaviorCharacteristics) {
		MinecraftMAPElitesBinLabels minecraftBinLabels = (MinecraftMAPElitesBinLabels) MMNEAT.getArchiveBinLabelsClass();
		final int SPACE_BETWEEN = Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes");
		MinecraftCoordinates startPosition;
		MinecraftCoordinates offset;
		int xOffset = (int) (((ranges.x() + SPACE_BETWEEN) / 2.0) - (ranges.x()/2.0)); 
		int yOffset = (int) (((ranges.y() + SPACE_BETWEEN) / 2.0) - (ranges.y()/2.0)); 
		int zOffset = (int) (((ranges.z() + SPACE_BETWEEN) / 2.0) - (ranges.z()/2.0)); 
		// Location in multi-dimensional archive
		int[] multiDimIndex = minecraftBinLabels.multiDimensionalIndices(behaviorCharacteristics);
		if(multiDimIndex.length==1 || multiDimIndex.length > 3 || Parameters.parameters.booleanParameter("forceLinearArchiveLayoutInMinecraft")) {
			// Derive 1D location from multi-dimensional location
			int index = (int) behaviorCharacteristics.get("dim1D");
			startPosition = new MinecraftCoordinates(index*(SPACE_BETWEEN+ranges.x()),MinecraftClient.GROUND_LEVEL+1,0);				
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
					"base:minecraft", "log:Minecraft-MAPElitesWHD", "saveTo:MAPElitesWHD",
					"io:true", "netio:true",
					"minecraftContainsWholeMAPElitesArchive:true","forceLinearArchiveLayoutInMinecraft:false",
					"launchMinecraftServerFromJava:false",
					//"io:false", "netio:false", 
					"mating:true", "fs:false", 
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
					"spaceBetweenMinecraftShapes:10",
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
}
