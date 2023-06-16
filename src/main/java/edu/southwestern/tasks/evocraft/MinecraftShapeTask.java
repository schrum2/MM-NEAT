package edu.southwestern.tasks.evocraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.BoundedIntegerValuedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.NetworkGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.BoundedTask;
import edu.southwestern.tasks.SinglePopulationTask;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;
import edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBinLabels;
import edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBlockCountBinLabels;
import edu.southwestern.tasks.evocraft.fitness.ChangeBlocksFitness;
import edu.southwestern.tasks.evocraft.fitness.ChangeCenterOfMassFitness;
import edu.southwestern.tasks.evocraft.fitness.DiversityBlockFitness;
import edu.southwestern.tasks.evocraft.fitness.FakeTestFitness;
import edu.southwestern.tasks.evocraft.fitness.MaximizeVolumeFitness;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;
import edu.southwestern.tasks.evocraft.fitness.MissileFitness;
import edu.southwestern.tasks.evocraft.fitness.NegativeSpaceCountFitness;
import edu.southwestern.tasks.evocraft.fitness.NumAirFitness;
import edu.southwestern.tasks.evocraft.fitness.OccupiedCountFitness;
import edu.southwestern.tasks.evocraft.fitness.RandomFitness;
import edu.southwestern.tasks.evocraft.fitness.TimedEvaluationMinecraftFitnessFunction;
import edu.southwestern.tasks.evocraft.fitness.TypeCountFitness;
import edu.southwestern.tasks.evocraft.fitness.TypeTargetFitness;
import edu.southwestern.tasks.evocraft.fitness.WaterLavaSecondaryCreationFitness;
import edu.southwestern.tasks.evocraft.shapegeneration.BoundedVectorGenerator;
import edu.southwestern.tasks.evocraft.shapegeneration.IntegersToVolumeGenerator;
import edu.southwestern.tasks.evocraft.shapegeneration.ShapeGenerator;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.file.FileUtilities;

/**
 *  MinecraftShapeTask is a class environment that lets you evaluate a phenotypes fitness geared towards minecraft shapes 
 * 
 * @author raffertyt
 *
 * @param <T> is the type of phenotype
 * 
 */
public class MinecraftShapeTask<T> implements SinglePopulationTask<T>, NetworkTask, BoundedTask {

	public static int VERTICAL_SAFETY_BUFFER = 20;

	// Visible within package
	ArrayList<MinecraftFitnessFunction> fitnessFunctions;
	private ArrayList<MinecraftCoordinates> corners;
	private int startingX;
	private int startingY;
	private int startingZ;

	// It's kind of bad for this to be static, but there should only every be one task running at a time, so it should be ok
	private static int numTimedFitnessFunctions;
	
	public static int getNumTimedFitnessFunctions() {
		return numTimedFitnessFunctions;
	}
	
	public static void setNumTimedFitnessFunctions(int num) {
		numTimedFitnessFunctions = num;
	}

	// Makes sure tiebreaking is run in the same way as before
	@SuppressWarnings("unchecked")
	public MinecraftShapeTask() {
		
		fitnessFunctions = defineFitnessFromParameters();
		numTimedFitnessFunctions = numTimedEvaluationMinecraftFitnessFunctions(fitnessFunctions);
		
		// Cannot allow random tie breaking since some generated shapes would be different
		Parameters.parameters.setBoolean("randomArgMaxTieBreak", false);
		CommonConstants.randomArgMaxTieBreak = false;

		if(numTimedFitnessFunctions != 0) {			//launch server if using timed fitness functions
			if(Parameters.parameters.booleanParameter("launchMinecraftServerFromJava")) {
				MinecraftServer.launchServer();
			}

			// Launches the client script before the parallel code to assure that only one client script exists
			MinecraftClient.getMinecraftClient();
		}

		// try catch for initialization error of NoSuchMethodException when creating block set	
		try {
			MMNEAT.blockSet = (BlockSet) ClassCreation.createObject("minecraftBlockSet");
		} catch (NoSuchMethodException e1) {
			System.out.println("Could not instantiate block set for Minecraft");
			e1.printStackTrace();
			System.exit(1);
		}

		if(Parameters.parameters.classParameter("genotype").equals(TWEANNGenotype.class)) {
			// This setting is used when evolving CPPNs that also maintain some information about the possible block types
			MMNEAT.discreteCeilings = ArrayUtil.intSpecified(Parameters.parameters.integerParameter("minecraftAmountOfBlocksToEvolve"),MMNEAT.blockSet.getPossibleBlocks().length);
		}
		
		// try catch for initialization error of NoSuchMethodException when creating shapeGenerator
		try {
			MMNEAT.shapeGenerator = (ShapeGenerator<T>) ClassCreation.createObject("minecraftShapeGenerator");
		} catch (NoSuchMethodException e) {
			System.out.println("Could not instantiate shape generator for Minecraft");
			e.printStackTrace();
			System.exit(1);
		}
		// Have to construct IntegersToVolumeGenerator before getting the discrete ceilings from it
		if(Parameters.parameters.classParameter("genotype").equals(BoundedIntegerValuedGenotype.class)) {
			System.out.println("Get discrete ceilings");
			MMNEAT.discreteCeilings = ((IntegersToVolumeGenerator) MMNEAT.shapeGenerator).getDiscreteCeilings();		
		}
		
		for(MinecraftFitnessFunction ff : fitnessFunctions) {
			MMNEAT.registerFitnessFunction(ff.getClass().getSimpleName());
		}		

		startingX = Parameters.parameters.integerParameter("startX");
		startingY = Parameters.parameters.integerParameter("startY");
		startingZ = Parameters.parameters.integerParameter("startZ");
	}

	/**
	 * Number of fitness functions that require simulation in the game for a certain amount of time.
	 * 
	 * @param fitnessFunctions list of fitness functions being used
	 * @return number that extend TimedEvaluationMinecraftFitnessFunction
	 */
	private int numTimedEvaluationMinecraftFitnessFunctions(ArrayList<MinecraftFitnessFunction> fitnessFunctions) {
		int total = 0;
		for(MinecraftFitnessFunction mff : fitnessFunctions) {
			if(mff instanceof TimedEvaluationMinecraftFitnessFunction) {
				total++;
			}
		}
		return total;
	}

	/**
	 * Creates an ArrayList filled with minecraft specific fitness functions
	 * @return fitness which is an array list filled with fitness functions
	 */
	public static ArrayList<MinecraftFitnessFunction> defineFitnessFromParameters() {
		ArrayList<MinecraftFitnessFunction> fitness = new ArrayList<MinecraftFitnessFunction>();

		if(Parameters.parameters.booleanParameter("minecraftTypeCountFitness")) {
			fitness.add(new TypeCountFitness());
		}

		if(Parameters.parameters.booleanParameter("minecraftTypeTargetFitness")) {
			fitness.add(new TypeTargetFitness());
		}

		if(Parameters.parameters.booleanParameter("minecraftOccupiedCountFitness")) {
			fitness.add(new OccupiedCountFitness());
		}

		if(Parameters.parameters.booleanParameter("minecraftDiversityBlockFitness")) {
			fitness.add(new DiversityBlockFitness());
		}

		if(Parameters.parameters.booleanParameter("minecraftChangeCenterOfMassFitness")) {
			fitness.add(new ChangeCenterOfMassFitness());
		}

		if(Parameters.parameters.booleanParameter("NegativeSpaceCountFitness")) {
			fitness.add(new NegativeSpaceCountFitness());
		}

		if(Parameters.parameters.booleanParameter("minecraftFakeTestFitness")) {
			fitness.add(new FakeTestFitness());
		}

		if(Parameters.parameters.booleanParameter("minecraftWaterLavaSecondaryCreationFitness")) {
			fitness.add(new WaterLavaSecondaryCreationFitness());
		}
		if(Parameters.parameters.booleanParameter("minecraftMaximizeVolumeFitness")) {
			fitness.add(new MaximizeVolumeFitness());
		}
		if(Parameters.parameters.booleanParameter("minecraftMissileFitness")) {
			fitness.add(new MissileFitness());
		}
		if(Parameters.parameters.booleanParameter("minecraftChangeBlocksFitness")) {
			fitness.add(new ChangeBlocksFitness());
		}
		if(Parameters.parameters.booleanParameter("minecraftRandomFitness")) {
			fitness.add(new RandomFitness());
		}
		if(Parameters.parameters.booleanParameter("minecraftNumAirFitness")) {
			fitness.add(new NumAirFitness());
		}
		System.out.println(fitness);
		
		return fitness;
	}

	/**
	 * get function for getting x coordinate of origin
	 * @return int that represents coordinates 
	 */
	public int getStartingX() { return startingX; }
	/**
	 * get function for getting Y coordinate of origin
	 * @return int that represents coordinates
	 */
	public int getStartingY() { return startingY; }
	/**
	 * get function for getting Z coordinate of origin
	 * @return int that represents coordinates
	 */
	public int getStartingZ() { return startingZ; }

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
		if(MMNEAT.genotype instanceof NetworkGenotype)
			MMNEAT.setNNInputParameters(sensorLabels().length, outputLabels().length);
	}

	@Override
	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {
		//MinecraftClient client = MinecraftClient.getMinecraftClient();		
		// Avoid recalculating the same corners every time
		if(corners == null) {
			corners = getShapeCorners(population.size(), startingX, startingY, startingZ, MinecraftUtilClass.getRanges());
		}

		// Must clear the space where shapes are placed
		// Clear the individually instead.
		//client.clearSpaceForShapes(new MinecraftCoordinates(startingX,MinecraftClient.GROUND_LEVEL+1,startingZ), MinecraftUtilClass.getRanges(), population.size(), Math.max(Parameters.parameters.integerParameter("minecraftMaxSnakeLength"), MinecraftClient.BUFFER));

		// Generate and evaluate shapes in parallel
		IntStream stream = IntStream.range(0, corners.size());
		ArrayList<Score<T>> scores = stream.parallel().mapToObj( i -> {
			MinecraftCoordinates corner = corners.get(i);
			MinecraftCoordinates middle = corner.add(MinecraftUtilClass.emptySpaceOffsets());
			Genotype<T> genome = population.get(i);
			return evaluateOneShape(genome, middle, fitnessFunctions);
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
		//System.out.println(genome.getId() + ":" + blocks);

		// Clear space around this one shape
		MinecraftLonerShapeTask.clearBlocksForShape(MinecraftUtilClass.getRanges(), corner.sub(MinecraftUtilClass.emptySpaceOffsets()));

		//MinecraftClient.getMinecraftClient().spawnBlocks(blocks);
		double[] fitnessScores = calculateFitnessScores(corner,fitnessFunctions,blocks);

		Score<T> score = new Score<T>(genome, fitnessScores);
		if(MMNEAT.usingDiversityBinningScheme) {
			//System.out.println("evaluate "+genome.getId() + " at " + corner + ": scores = "+ Arrays.toString(fitnessScores));

			MinecraftMAPElitesBinLabels minecraftBinLabels = (MinecraftMAPElitesBinLabels) MMNEAT.getArchiveBinLabelsClass();
			// It is important to note that the original blocks from the CPPN are used here rather than the blocks
			// read from the world. So, any properties collected will be before movement due to machine parts.
			double[] propertyScores = calculateFitnessScores(corner,minecraftBinLabels.properties(), blocks);
			// Map contains all required properties now
			HashMap<String,Object> behaviorMap = minecraftBinLabels.behaviorMapFromScores(propertyScores);

			double binScore = qualityScore(fitnessScores); 
			behaviorMap.put("binScore", binScore); // Quality Score!				

			assert !behaviorMap.containsKey("WidthFitness") || ((Double) behaviorMap.get("WidthFitness")).doubleValue() <= Parameters.parameters.integerParameter("minecraftXRange") : genome.getId() +":"+ behaviorMap + ":" + blocks + ":" + corner;

			// Do this last
			int dim1D = minecraftBinLabels.oneDimensionalIndex(behaviorMap);
			behaviorMap.put("dim1D", dim1D); // Save so it does not need to be computed again
			score.assignMAPElitesBehaviorMapAndScore(behaviorMap);

			//if(genome.getId()  == 91) System.out.println(genome.getId() + ":" + blocks + ":" + behaviorMap);
			assert !(minecraftBinLabels instanceof MinecraftMAPElitesBlockCountBinLabels) || ((Integer) behaviorMap.get("dim1D")).intValue() == (int) ((Double) behaviorMap.get("OccupiedCountFitness")).doubleValue() - 1 : behaviorMap + ":" + blocks;
			assert !(minecraftBinLabels instanceof MinecraftMAPElitesBlockCountBinLabels) || blocks.size() == (int) ((Double) behaviorMap.get("OccupiedCountFitness")).doubleValue() : behaviorMap + ":" + blocks;
		} 

		//TODO: calls certainFlying and saves the shapes
		// If the new shapeIsWorthSaving method in TimedEvaluationMinecraftFitnessFunction works, then the code below is not needed,
		// since we don't need to save shapes twice.
		
		//makes a directory, and writes block list
		if(CommonConstants.netio && Parameters.parameters.booleanParameter("minecraftChangeCenterOfMassFitness") && certainFlying(fitnessFunctions, fitnessScores[0])) {
			// Assuming that change in center of mass is at index 0, and that 5 is a suitable threshold for penalties to the max fitness
			String flyingDir = FileUtilities.getSaveDirectory() + "/flyingMachines";
			File dir = new File(flyingDir);	// Create dir
			if (!dir.exists()) {
				dir.mkdir();
			}
			//Orientation flyingDirection = directionOfMaximumDisplacement(deltaX,deltaY,deltaZ);
			//String gen = "GEN"+(MMNEAT.ea instanceof GenerationalEA ? ((GenerationalEA) MMNEAT.ea).currentGeneration() : "ME");
			MinecraftLonerShapeTask.writeBlockListFile(blocks, flyingDir + File.separator + "ID"+genome.getId(), ".txt");
		}
		if(Parameters.parameters.integerParameter("minecraftDelayAfterEvaluation")> 0) {
			try {
				Thread.sleep(Parameters.parameters.integerParameter("minecraftDelayAfterEvaluation"));
			} catch (InterruptedException e) {
				System.out.println("Exception from interrupting sleep between shape placements!");
				e.printStackTrace();
				System.exit(1);
			}
		}
		return score;
	}
	/**
	 * Uses the fitness score to determine if the machine is truly flying
	 * @param fitnessScore the fitness score of the machine being evaluated
	 * @return boolean that returns true if the machine is actually flying 
	 */
	public boolean certainFlying(double fitnessScore) {
		return certainFlying(fitnessFunctions, fitnessScore);
	}

	/**
	 * A shape with a fitness of this amount must be flying (assumes first/only fitness is change in center of mass)
	 * 
	 * @param fitnessFunctions list of fitnessFunctons
	 * @param fitnessScores the list of fitness scores of each function in fitness function of the machine being evaluated
	 * @return boolean true if the fitnessScore is greater than max fitness - flying penalty buffer
	 */
	public static boolean certainFlying(ArrayList<MinecraftFitnessFunction> fitnessFunctions, double fitnessScore) {
		assert fitnessFunctions.get(0) instanceof ChangeCenterOfMassFitness;
		assert Parameters.parameters.booleanParameter("minecraftChangeCenterOfMassFitness");
		return fitnessScore > fitnessFunctions.get(0).maxFitness() - ChangeCenterOfMassFitness.FLYING_PENALTY_BUFFER;
	}

	/**
	 * Gets quality score used by MAP Elites. Is currently
	 * just the first fitness score, but we may want a more sophisticated way
	 * to designate this in the future
	 * @param fitnessScores All calculated fitness scores
	 * @return Single quality score
	 */

	public static double qualityScore(double[] fitnessScores) {
		return fitnessScores[0]; // TODO: CHANGE THIS? Other code depends on it now
	}

	/**
	 * Calculate all fitness scores for a shape at a given corner
	 *  
	 * @param shapeCorner Minimal corner from which shape is generated
	 * @param fitnessFunctions Fitness functions to calculate
	 * @param numTimedFitnessFunctions Number that require timed evaluation
	 * @param originalBlocks blocks from generator, before rendering
	 * @return double array of all fitness values in order
	 */
	public static double[] calculateFitnessScores(MinecraftCoordinates shapeCorner, List<MinecraftFitnessFunction> fitnessFunctions, List<Block> originalBlocks) {
		//create separate lists for the TimedEvaluationMinecraftFitnessFunctions and MinecraftFitnessFunctions
		List<TimedEvaluationMinecraftFitnessFunction> timedEvaluationFitnessFunctionsList = new ArrayList<TimedEvaluationMinecraftFitnessFunction>(numTimedFitnessFunctions);
		List<MinecraftFitnessFunction> notTimedFitnessFunctionsList = new ArrayList<MinecraftFitnessFunction>(fitnessFunctions.size()-numTimedFitnessFunctions);
		
		//sort through the passed fitness functions to separate the TimedEvaluationMinecraftFitnessFunctions from the not timed fitness functions into two lists
		for(MinecraftFitnessFunction mff : fitnessFunctions) {
			if(mff instanceof TimedEvaluationMinecraftFitnessFunction) {
				timedEvaluationFitnessFunctionsList.add((TimedEvaluationMinecraftFitnessFunction) mff);
			} else {
				notTimedFitnessFunctionsList.add(mff);
			}
		}	

		assert fitnessFunctions.size() == timedEvaluationFitnessFunctionsList.size() + notTimedFitnessFunctionsList.size() : fitnessFunctions + " should match \n" + timedEvaluationFitnessFunctionsList + " and " + notTimedFitnessFunctionsList;
		
		//concatenate both lists here, a list must be made and then combined in a new list
		double[] timedEvalResults = numTimedFitnessFunctions == 0 ? new double[0] : TimedEvaluationMinecraftFitnessFunction.multipleFitnessScores(timedEvaluationFitnessFunctionsList, shapeCorner, originalBlocks);
		double[] notTimedEvalResults = notTimedFitnessFunctionsList.parallelStream().mapToDouble(ff -> ff.fitnessScore(shapeCorner,originalBlocks)).toArray();
		return ArrayUtil.combineArrays(timedEvalResults, notTimedEvalResults);

	}

	/**
	 * Generate a given number of spawn corners for shapes based on given starting x/y/z coordinates
	 * and x/y/z-ranges for shape generation. All 3 can be altered with command line params
	 * 
	 * @param size Size of population, and thus number of corners to create
	 * @param startingX x-coordinate of corner for first shape
	 * @param startingY y-coordinate of corner for first shape
	 * @param startingZ z-coordinate of corner for first shape
	 * @param ranges size of generated shapes in x/y/z dimensions
	 */
	public static ArrayList<MinecraftCoordinates> getShapeCorners(int size, int startingX, int startingY, int startingZ, MinecraftCoordinates ranges) {
		ArrayList<MinecraftCoordinates> corners = new ArrayList<>(size);
		int extraSpace = Parameters.parameters.integerParameter("extraSpaceBetweenMinecraftShapes");
		int totalSpaceBetweenShapes = Math.max(Math.max(ranges.x(), ranges.y()), ranges.z()) + Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes") + extraSpace;

		// If placing diagonally, decrease the x and z coordinates. Increase the Y
		if(Parameters.parameters.booleanParameter("displayDiagonally")) {
			int yCoordinate = startingY;
			for(int i = 0; i < size; i++) {
				while(yCoordinate + ranges.y() + Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes")+VERTICAL_SAFETY_BUFFER >= MinecraftClient.MAX_Y_COORDINATE) {
					// Y values will spike up but reset when out of range (like sawtooth function)
					yCoordinate = Math.max(startingY, yCoordinate - MinecraftClient.MAX_Y_COORDINATE);
				}
				MinecraftCoordinates corner = new MinecraftCoordinates(startingX - i*totalSpaceBetweenShapes,yCoordinate, startingZ - i*totalSpaceBetweenShapes);
				System.out.println("Evaluation Corner: "+ corner);
				corners.add(corner);
				yCoordinate += totalSpaceBetweenShapes;
			}
			// Otherwise, generate in line
		}else {
			for(int i = 0; i < size; i++) {
				MinecraftCoordinates corner = new MinecraftCoordinates(startingX + i*totalSpaceBetweenShapes, startingY, startingZ);				
				corners.add(corner);
			}
		}

		return corners;
	}
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		MMNEAT.main("runNumber:89 randomSeed:89 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftDiversityBlockFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.SimpleSolidBlockSet trials:1 mu:20 maxGens:3005 launchMinecraftServerFromJava:false io:true netio:true mating:true fs:false spaceBetweenMinecraftShapes:7 task:edu.southwestern.tasks.evocraft.MinecraftShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:minecraftaccumulate log:MinecraftAccumulate-ESVectorSimple saveTo:ESVectorSimple".split(" ")); 
	}

	@Override
	public double[] getUpperBounds() {
		return ((BoundedVectorGenerator) MMNEAT.shapeGenerator).getUpperBounds(); // calling getUpperBounds from VectorToVolumeGenerator class

	}

	@Override
	public double[] getLowerBounds() {
		return ((BoundedVectorGenerator) MMNEAT.shapeGenerator).getLowerBounds(); // calling getlowerBounds from VectorToVolumeGenerator class
	}
}
