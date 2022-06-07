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
			
			//Checks if the bin either 1S 4d+ or if command line param for linear archive is true. If any are, Generates them in 1 dimension
			if(minecraftBinLabels.dimensionSizes().length==1 ||minecraftBinLabels.dimensionSizes().length>=4||Parameters.parameters.booleanParameter("forceLinearArchiveLayoutInMinecraft")) {
				
				System.out.println("==================================================================================================");
				// Get the one dimensional index of the shape
				int oneDimIndex = minecraftBinLabels.oneDimensionalIndex(behaviorCharacteristics);
				System.out.println("1D index: "+oneDimIndex);
				
				// Gets the bin scores to compare them 
				double scoreOfCurrentElite = (double) behaviorCharacteristics.get("binScore");
				@SuppressWarnings("unchecked")
				double scoreOfPreviousElite = ((MAPElites<T>) MMNEAT.ea).getArchive().getBinScore(oneDimIndex);
				
				MinecraftCoordinates startPosition = new MinecraftCoordinates(oneDimIndex*MinecraftClient.BUFFER+oneDimIndex*ranges.x(),5,0);
				System.out.println("Starting position: "+startPosition);
				System.out.println("CURRENT: "+scoreOfCurrentElite+" |PREVIOUS: "+scoreOfPreviousElite);
				if(scoreOfCurrentElite>scoreOfPreviousElite) {
					MinecraftClient.getMinecraftClient().clearSpaceForShapes(startPosition, ranges, 1, MinecraftClient.BUFFER);
//					List<Block> test = new ArrayList<>();
//					test.add(new Block(startPosition.x(),5,0,BlockType.GLOWSTONE, Orientation.WEST));
//					test.add(new Block(startPosition.x()+1,5,0,BlockType.GLOWSTONE, Orientation.WEST));
//					test.add(new Block(startPosition.x()+1,6,0,BlockType.GLOWSTONE, Orientation.WEST));
//					test.add(new Block(startPosition.x()+1,6,1,BlockType.GLOWSTONE, Orientation.WEST));
//					test.add(new Block(startPosition.x(),6,0,BlockType.GLOWSTONE, Orientation.WEST));
//					test.add(new Block(startPosition.x(),6,1,BlockType.GLOWSTONE, Orientation.WEST));
//					test.add(new Block(startPosition.x()+1,5,1,BlockType.GLOWSTONE, Orientation.WEST));
//					test.add(new Block(startPosition.x(),5,1,BlockType.GLOWSTONE, Orientation.WEST));
					@SuppressWarnings("unchecked")
					List<Block> blocks = MMNEAT.shapeGenerator.generateShape(individual, startPosition, MMNEAT.blockSet);
					MinecraftClient.getMinecraftClient().spawnBlocks(blocks);
				}
			}
			else {
				assert minecraftBinLabels.dimensionSizes().length== 3;
				int[] multiDimIndex = minecraftBinLabels.multiDimensionalIndices(behaviorCharacteristics);
				MinecraftCoordinates startPosition = new MinecraftCoordinates(multiDimIndex[0]*Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes")+multiDimIndex[0]*ranges.x(),multiDimIndex[1]*Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes")+multiDimIndex[1]*ranges.y(),multiDimIndex[2]*Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes")+multiDimIndex[2]*ranges.z());
				System.out.println(startPosition);
				
				int oneDimIndex = minecraftBinLabels.oneDimensionalIndex(behaviorCharacteristics);
				double scoreOfCurrentElite = (double) behaviorCharacteristics.get("binScore");
				@SuppressWarnings("unchecked")
				double scoreOfPreviousElite = ((MAPElites<T>) MMNEAT.ea).getArchive().getBinScore(oneDimIndex);
				
				if(scoreOfCurrentElite>scoreOfPreviousElite) {
//					MinecraftClient.getMinecraftClient().clearSpaceForShapes(startPosition, ranges, 1, 1,false);
					MinecraftCoordinates bufferDist = new MinecraftCoordinates(Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes")-1);
					MinecraftCoordinates clearStart = startPosition.sub(bufferDist);
					MinecraftCoordinates clearEnd = startPosition.add(bufferDist).add(ranges);
					MinecraftClient.getMinecraftClient().fillCube(clearStart, clearEnd, BlockType.AIR);
					System.out.println("AAAAAAAAAAAAAAAAAAAAA"+clearEnd);
					
//					List<Block> test = new ArrayList<>();
//					test.add(new Block(startPosition.x(),startPosition.y(),startPosition.z(),BlockType.GLOWSTONE, Orientation.WEST));
//					test.add(new Block(startPosition.x()+1,startPosition.y(),startPosition.z(),BlockType.GLOWSTONE, Orientation.WEST));
//					test.add(new Block(startPosition.x()+1,startPosition.y()+1,startPosition.z(),BlockType.GLOWSTONE, Orientation.WEST));
//					test.add(new Block(startPosition.x()+1,startPosition.y()+1,startPosition.z()+1,BlockType.GLOWSTONE, Orientation.WEST));
//					test.add(new Block(startPosition.x(),startPosition.y()+1,startPosition.z()+1,BlockType.GLOWSTONE, Orientation.WEST));
//					test.add(new Block(startPosition.x(),startPosition.y()+1,startPosition.z(),BlockType.GLOWSTONE, Orientation.WEST));
//					test.add(new Block(startPosition.x(),startPosition.y(),startPosition.z()+1,BlockType.GLOWSTONE, Orientation.WEST));
//					test.add(new Block(startPosition.x()+1,startPosition.y(),startPosition.z()+1,BlockType.GLOWSTONE, Orientation.WEST));
//					
					@SuppressWarnings("unchecked")
					List<Block> blocks = MMNEAT.shapeGenerator.generateShape(individual, startPosition, MMNEAT.blockSet);
					MinecraftClient.getMinecraftClient().spawnBlocks(blocks);
				}
			}			
			// This result will be ignored when using MAP Elites
			}
			
		return new Pair<>(score.scores, score.otherStats);
	}
	
//	public static int[] worldArchiveCoordinates(HashMap<String, Object> behaviorCharacteristics) {
//		
//	}
	
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
					"minecraftXRange:2","minecraftYRange:2","minecraftZRange:2",
					"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.SnakeGenerator",
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
