package edu.southwestern.tasks.evocraft;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.SinglePopulationTask;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;
import edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBinLabels;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.fitness.ChangeCenterOfMassFitness;
import edu.southwestern.tasks.evocraft.fitness.CheckBlocksInSpaceFitness;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;
import edu.southwestern.tasks.evocraft.fitness.OccupiedCountFitness;
import edu.southwestern.tasks.evocraft.fitness.TypeCountFitness;
import edu.southwestern.tasks.evocraft.fitness.TypeTargetFitness;
import edu.southwestern.tasks.evocraft.fitness.DiversityBlockFitness;
import edu.southwestern.tasks.evocraft.shapegeneration.ShapeGenerator;
import edu.southwestern.util.ClassCreation;


public class MinecraftShapeTask<T> implements SinglePopulationTask<T>, NetworkTask {
	
	private ArrayList<MinecraftFitnessFunction> fitnessFunctions;
	private ArrayList<MinecraftCoordinates> corners;
	private int startingX;
	private int startingZ;
	private MinecraftCoordinates ranges;
	
	@SuppressWarnings("unchecked")
	public MinecraftShapeTask() {
		if(Parameters.parameters.booleanParameter("launchMinecraftServerFromJava")) {
			MinecraftServer.launchServer();
		}
		// Launches the client script before the parallel code to assure that only one client script exists
		MinecraftClient.getMinecraftClient();
		
		fitnessFunctions = new ArrayList<MinecraftFitnessFunction>();

		if(Parameters.parameters.booleanParameter("minecraftTypeCountFitness")) {
			fitnessFunctions.add(new TypeCountFitness());
		}
		
		if(Parameters.parameters.booleanParameter("minecraftTypeTargetFitness")) {
			fitnessFunctions.add(new TypeTargetFitness());
		}
		
		if(Parameters.parameters.booleanParameter("minecraftOccupiedCountFitness")) {
			fitnessFunctions.add(new OccupiedCountFitness());
		}
		
		if(Parameters.parameters.booleanParameter("minecraftDiversityBlockFitness")) {
			fitnessFunctions.add(new DiversityBlockFitness());
		}
		
		if(Parameters.parameters.booleanParameter("minecraftChangeCenterOfMassFitness")) {
			fitnessFunctions.add(new ChangeCenterOfMassFitness());
		}
		
		try {
			MMNEAT.blockSet = (BlockSet) ClassCreation.createObject("minecraftBlockSet");
		} catch (NoSuchMethodException e1) {
			System.out.println("Could not instantiate block set for Minecraft");
			e1.printStackTrace();
			System.exit(1);
		}

		try {
			MMNEAT.shapeGenerator = (ShapeGenerator<T>) ClassCreation.createObject("minecraftShapeGenerator");
		} catch (NoSuchMethodException e) {
			System.out.println("Could not instantiate shape generator for Minecraft");
			e.printStackTrace();
			System.exit(1);
		}
		for(MinecraftFitnessFunction ff : fitnessFunctions) {
			MMNEAT.registerFitnessFunction(ff.getClass().getSimpleName());
		}		
		
		startingX = 0;
		startingZ = 0;
		ranges = new MinecraftCoordinates(
				Parameters.parameters.integerParameter("minecraftXRange"),
				Parameters.parameters.integerParameter("minecraftYRange"),
				Parameters.parameters.integerParameter("minecraftZRange"));
	}
	
	public int getStartingX() { return startingX; }
	
	public int getStartingZ() { return startingZ; }
	
	public MinecraftCoordinates getRanges() { return ranges; }
	
	/**
	 * returns the sensorLabels
	 * @return the sensorLabels
	 */
	@Override
	public String[] sensorLabels() {
		if(Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane")) {
			return new String[] { "X", "Y", "Z", "R", "R-XY", "R-YZ", "R-XZ", "bias" };
		} else {
			return new String[] { "X", "Y", "Z", "R", "bias" };
		}
	}

	/**
	 * returns the outputLabels
	 * @return the outputLabels
	 */
	@Override
	public String[] outputLabels() {
		return MMNEAT.shapeGenerator.getNetworkOutputLabels();
	}

	@Override
	public int numObjectives() {
		return fitnessFunctions.size();
	}

	@Override
	public double[] minScores() {
		double[] scores = new double[fitnessFunctions.size()];
		for(int i = 0; i < scores.length; i++) {
			scores[i] = fitnessFunctions.get(i).minFitness();
		}
		return scores;
	}

	@Override
	public double getTimeStamp() {
		return 0; // Not used
	}

	@Override
	public void finalCleanup() {
		MinecraftClient.terminateClientScriptProcess();
		// Close Minecraft server after all evolution is done
		if(Parameters.parameters.booleanParameter("launchMinecraftServerFromJava")) {
			MinecraftServer.terminateServer();
		}
	}

	@Override
	public void postConstructionInitialization() {
		MMNEAT.setNNInputParameters(sensorLabels().length, outputLabels().length);
	}

	@Override
	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {
		MinecraftClient client = MinecraftClient.getMinecraftClient();		
		// Avoid recalculating the same corners every time
		if(corners == null) {
			corners = getShapeCorners(population.size(), startingX, startingZ, ranges);
		}

		// Must clear the space where shapes are placed
		client.clearSpaceForShapes(new MinecraftCoordinates(startingX,MinecraftClient.GROUND_LEVEL+1,startingZ), ranges, population.size(), Math.max(Parameters.parameters.integerParameter("minecraftMaxSnakeLength"), MinecraftClient.BUFFER));
		
		// Generate and evaluate shapes in parallel
		IntStream stream = IntStream.range(0, corners.size());
		ArrayList<Score<T>> scores = stream.parallel().mapToObj( i -> {
			MinecraftCoordinates corner = corners.get(i);
			Genotype<T> genome = population.get(i);
			return evaluateOneShape(genome, corner, fitnessFunctions);
		}).collect(Collectors.toCollection(ArrayList::new));
		System.out.println("Finished collecting");
		return scores;
	}
	
	/**
	 * For one genome at one corner location, spawn the blocks and calculate the fitness before returning the Score.
	 * Fitness functions come from the instance variable.
	 * 
	 * @param genome Calculate fitness of this genome
	 * @param corner After generating shape at this corner
	 * @return Return the Score containing fitness information and more
	 */
	public Score<T> evaluateOneShape(Genotype<T> genome, MinecraftCoordinates corner) {
		return evaluateOneShape(genome, corner, fitnessFunctions);
	}
	
	/**
	 * For one genome at one corner location, spawn the blocks and calculate the fitness before returning the Score
	 * 
	 * @param <T>
	 * @param genome Evolved individual that generates a shape
	 * @param corner Location to generate shape at: minimal coordinate
	 * @param fitnessFunctions List of fitness functions to evaluate the shape on
	 * @return Score instance containing evaluation information
	 */
	public static <T> Score<T> evaluateOneShape(Genotype<T> genome, MinecraftCoordinates corner, ArrayList<MinecraftFitnessFunction> fitnessFunctions) {
		@SuppressWarnings("unchecked")
		List<Block> blocks = MMNEAT.shapeGenerator.generateShape(genome, corner, MMNEAT.blockSet);
		MinecraftClient.getMinecraftClient().spawnBlocks(blocks);
		double[] fitnessScores = calculateFitnessScores(corner,fitnessFunctions);
		Score<T> score = new Score<T>(genome, fitnessScores);
		if(MMNEAT.usingDiversityBinningScheme) {
			MinecraftMAPElitesBinLabels minecraftBinLabels = (MinecraftMAPElitesBinLabels) MMNEAT.getArchiveBinLabelsClass();
			// It is important to note that the original blocks from the CPPN are used here rather than the blocks
			// read from the world. So, any properties collected will be before movement due to machine parts.
			double[] propertyScores = calculateFitnessScores(corner,minecraftBinLabels.properties(),blocks);
			// Map contains all required properties now
			HashMap<String,Object> behaviorMap = minecraftBinLabels.behaviorMapFromScores(propertyScores);
			
			double binScore = fitnessScores[0]; // TODO: CHANGE THIS!
			behaviorMap.put("binScore", binScore); // Quality Score!				
			// Do this last
			int dim1D = minecraftBinLabels.oneDimensionalIndex(behaviorMap);
			behaviorMap.put("dim1D", dim1D); // Save so it does not need to be computed again
			score.assignMAPElitesBehaviorMapAndScore(behaviorMap);
		}
		return score;
	}

	/**
	 * Calculate all fitness scores for a shape at a given corner.
	 * This makes sure that the blocks actually come from the Minecraft world.
	 * 
	 * @param corner Minimal corner from which shape is generated
	 * @param fitnessFunctions the shape properties to calculate
	 * @return double array of all fitness values in order
	 */
	public static double[] calculateFitnessScores(MinecraftCoordinates corner, List<MinecraftFitnessFunction> fitnessFunctions) {
		List<Block> readBlocks = CheckBlocksInSpaceFitness.readBlocksFromClient(corner); // Read these just once
		return calculateFitnessScores(corner, fitnessFunctions, readBlocks);
	}

	/**
	 * Calculate all fitness scores for a shape at a given corner
	 * 
	 * @param corner Minimal corner from which shape is generated
	 * @return double array of all fitness values in order
	 */
	public static double[] calculateFitnessScores(MinecraftCoordinates corner, List<MinecraftFitnessFunction> fitnessFunctions, List<Block> readBlocks) {
		// Parallelize fitness calculation
		double[] fitnessScores = fitnessFunctions.parallelStream().mapToDouble(ff -> {
			if(ff instanceof CheckBlocksInSpaceFitness) {
				// All fitness functions of this type can just use the previously computed readBlocks list
				return ((CheckBlocksInSpaceFitness) ff).fitnessFromBlocks(corner,readBlocks);
			} else {
				return ff.fitnessScore(corner);
			}
		}).toArray();
		
		return fitnessScores;
	}

	/**
	 * Generate a given number of spawn corners for shapes based on given starting x/z coordinates
	 * (starting y is assumed to be relative to the ground), and x/y/z-ranges for shape generation.
	 * 
	 * @param size Size of population, and thus number of corners to create
	 * @param startingX x-coordinate of corner for first shape
	 * @param startingZ z-coordinate of corner for first shape
	 * @param ranges size of generated shapes in x/y/z dimensions
	 */
	public static ArrayList<MinecraftCoordinates> getShapeCorners(int size, int startingX, int startingZ, MinecraftCoordinates ranges) {
		ArrayList<MinecraftCoordinates> corners = new ArrayList<>(size);
		int count = 0;
		for(int i = 0; i < size; i++) {
			MinecraftCoordinates corner = new MinecraftCoordinates(startingX + count*(ranges.x() + Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes")), MinecraftClient.GROUND_LEVEL+1, startingZ);
			corners.add(corner);
			count++;
		}
		return corners;
	}

	public static void main(String[] args) {
		int seed = 0;
		try {
			MMNEAT.main(new String[] { "runNumber:" + seed, "randomSeed:" + seed, "trials:1", "mu:10", "maxGens:100",
					"base:minecraft", "log:Minecraft-CenterOfMass", "saveTo:CenterOfMass",
					"io:true", "netio:true", 
					//"io:false", "netio:false", 
					"mating:true", "fs:false", 
					"launchMinecraftServerFromJava:false",
					//"minecraftTypeCountFitness:true",
					//"minecraftTypeTargetFitness:true", 
					//"minecraftDesiredBlockCount:40",
					//"minecraftOccupiedCountFitness:true",
					"minecraftChangeCenterOfMassFitness:true",
					//"minecraftDiversityBlockFitness:true",
					//"minecraftEvolveOrientation:true",
					//"minecraftRedirectConfinedSnakes:true",
					//"minecraftStopConfinedSnakes:true",
					"minecraftXRange:1", "minecraftYRange:2", "minecraftZRange:5",
					//"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.SnakeGenerator",
					"task:edu.southwestern.tasks.evocraft.MinecraftShapeTask", "allowMultipleFunctions:true",
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
