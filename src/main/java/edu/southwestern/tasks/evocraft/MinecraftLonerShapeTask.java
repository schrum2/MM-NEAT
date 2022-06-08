package edu.southwestern.tasks.evocraft;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.BaseBinLabels;
import edu.southwestern.evolution.mapelites.MAPElites;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
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
			
			// Creates the bin labels
			MinecraftMAPElitesBinLabels minecraftBinLabels = (MinecraftMAPElitesBinLabels) MMNEAT.getArchiveBinLabelsClass();
			System.out.println(minecraftBinLabels.dimensionSizes().length);
			
			// Places the shapes in the world based on their position
			placeArchiveInWorld(minecraftBinLabels.dimensionSizes().length,individual, behaviorCharacteristics, ranges, minecraftBinLabels);		
		}
		// This result will be ignored when using MAP Elites	
		return new Pair<>(score.scores, score.otherStats);
	}

	/**
	 * 
	 * @param dimSize
	 * @param individual
	 * @param behaviorCharacteristics
	 * @param ranges
	 * @param minecraftBinLabels
	 */
	public void placeArchiveInWorld(int dimSize,Genotype<T> individual, HashMap<String, Object> behaviorCharacteristics,
			MinecraftCoordinates ranges, MinecraftMAPElitesBinLabels minecraftBinLabels) {
		// Gets the multi-dimensional index for starting points
		int oneDimIndex = minecraftBinLabels.oneDimensionalIndex(behaviorCharacteristics);
		
		// Starting position is different for each dimension size
		MinecraftCoordinates startPosition;
		if (dimSize==3 && !Parameters.parameters.booleanParameter("forceLinearArchiveLayoutInMinecraft")){
			int[] multiDimIndex = minecraftBinLabels.multiDimensionalIndices(behaviorCharacteristics);
			startPosition = new MinecraftCoordinates(multiDimIndex[0]*Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes")+multiDimIndex[0]*ranges.x(),multiDimIndex[1]*Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes")+multiDimIndex[1]*ranges.y(),multiDimIndex[2]*Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes")+multiDimIndex[2]*ranges.z());
		}else if(dimSize==2 && !Parameters.parameters.booleanParameter("forceLinearArchiveLayoutInMinecraft")){
			int[] multiDimIndex = minecraftBinLabels.multiDimensionalIndices(behaviorCharacteristics);
			startPosition = new MinecraftCoordinates(multiDimIndex[0]*Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes")+multiDimIndex[0]*ranges.x(),5,multiDimIndex[2]*Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes")+multiDimIndex[2]*ranges.z());
		}else {
			startPosition = new MinecraftCoordinates(oneDimIndex*MinecraftClient.BUFFER+oneDimIndex*ranges.x(),5,0);
		}
		
		// Gets the bin scores to compare them
		double scoreOfCurrentElite = (double) behaviorCharacteristics.get("binScore");
		@SuppressWarnings("unchecked")
		double scoreOfPreviousElite = ((MAPElites<T>) MMNEAT.ea).getArchive().getBinScore(oneDimIndex);
		
		// If the new shape is better than the previous, it gets replaced
		if(scoreOfCurrentElite>scoreOfPreviousElite && scoreOfPreviousElite>0) {
			clearBlocksInArchive(dimSize, ranges, startPosition);
			
			@SuppressWarnings("unchecked")
			List<Block> blocks = MMNEAT.shapeGenerator.generateShape(individual, startPosition, MMNEAT.blockSet);
			MinecraftClient.getMinecraftClient().spawnBlocks(blocks);
		}
	}

	/**
	 * 
	 * @param dimSize
	 * @param ranges
	 * @param startPosition
	 */
	public void clearBlocksInArchive(int dimSize,MinecraftCoordinates ranges, MinecraftCoordinates startPosition) {
		MinecraftCoordinates bufferDist = new MinecraftCoordinates(Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes")-1,1,Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes")-1);
		
		MinecraftCoordinates clearStart = startPosition.sub(bufferDist);
		if(dimSize!=3 || Parameters.parameters.booleanParameter("forceLinearArchiveLayoutInMinecraft")) {
			bufferDist = bufferDist.add(new MinecraftCoordinates(0,3,0));
		}
		MinecraftCoordinates clearEnd = startPosition.add(bufferDist).add(ranges);
		MinecraftClient.getMinecraftClient().fillCube(clearStart, clearEnd, BlockType.AIR);
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
					"minecraftContainsWholeMAPElitesArchive:true","forceLinearArchiveLayoutInMinecraft:true",
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
					"mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBlockCountBinLabels",
					"ea:edu.southwestern.evolution.mapelites.MAPElites", 
					"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
					"steadyStateIndividualsPerGeneration:100", 
					//FOR TESTING
					"minecraftXRange:2","minecraftYRange:2","minecraftZRange:2",
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
