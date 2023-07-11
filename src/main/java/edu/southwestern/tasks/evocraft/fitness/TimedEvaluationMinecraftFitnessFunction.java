package edu.southwestern.tasks.evocraft.fitness;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.file.FileUtilities;

/**
 * An abstract class to handle timed evaluation fitness functions that can be extended to other functions
 * currently contains code for clearing blocks, spawning blocks, creating a history list of readings with time stamps, and returning a call to calculateFinalFitnessScore
 * uses/creates:
 * history - a list of time stamps with an associated list of blocks read at that time
 * methods: fitnessScore, calculateFinalScore
 * @author lewisj
 *
 */
public abstract class TimedEvaluationMinecraftFitnessFunction extends MinecraftFitnessFunction{

	// Tracks number of shapes saved during evolution
	private static int savedShapes = 0;
	
	@Override
	public double fitnessScore(MinecraftCoordinates shapeCorner, List<Block> originalBlocks) {
		// For a scenario using just one fitness function, put this function in a list by itself and use the multiple evaluation method on the list of one
		ArrayList<TimedEvaluationMinecraftFitnessFunction> fitnessFunctions = new ArrayList<>(1);
		fitnessFunctions.add(this);
		double[] scores = multipleFitnessScores(fitnessFunctions, shapeCorner, originalBlocks);
		return scores[0]; // Only score from list of one fitness function
	}
	
	/**
	 * Collects scores for multiple TimedEvaluationMinecraftFitnessFunctions, but only spawns and evaluates the shape once.
	 * Evaluation can end early if all fitness functions agree to end early. Otherwise, early termination scores are stored
	 * for those that wanted to stop until the end, when all remaining fitness scores are calculated in the normal fashion.
	 * 
	 * @param fitnessFunctions List of TimedEvaluationMinecraftFitnessFunction type fitness functions
	 * @param shapeCorner Minimal corner where shape it spawned
	 * @param originalBlocks Original block configuration of shape before simulation
	 * @return array where each index if a fitness score for the corresponding fitness function
	 */
	public static double[] multipleFitnessScores(List<TimedEvaluationMinecraftFitnessFunction> fitnessFunctions, MinecraftCoordinates shapeCorner, List<Block> originalBlocks) {		
		originalBlocks = MinecraftUtilClass.filterOutBlock(originalBlocks, BlockType.AIR);
		
		// Should this be true for all fitness functions?
		if(originalBlocks.isEmpty()) {
			if(CommonConstants.watch) System.out.println("Empty shape: Immediate failure");
			return fitnessFunctions.parallelStream().mapToDouble(ff -> ff.minFitness()).toArray();
		}		

		// Ranges before the change of space in between
		int xrange = Parameters.parameters.integerParameter("minecraftXRange");
		//int yrange = Parameters.parameters.integerParameter("minecraftYRange");
		int zrange = Parameters.parameters.integerParameter("minecraftZRange");

		assert xrange > 0 : "xrange must be positive: " + xrange;
		assert zrange > 0 : "zrange must be positive: " + zrange;

		// Shifts over the corner to the new range with the large space in between shapes
		MinecraftCoordinates evaluationCorner = shapeCorner.sub(MinecraftUtilClass.emptySpaceOffsets());
		// schrum2: I think this code is responsible for the weird error of shapes near the ground being stacked vertically.
		//          When the startY is made large enough, this is not an issue, but making the user set that correctly
		//          is a hassle.		
//	PUSH UP FROM GROUND : PUSHUP CODE
		//if statement checks if the evaluation space plus the space that would be cleared is below the ground level
		if(evaluationCorner.y() - Parameters.parameters.integerParameter("minecraftEmptySpaceBufferY") <= MinecraftClient.GROUND_LEVEL) { // Push up if close to ground
			System.out.println("Pushed up from " + evaluationCorner);
			MinecraftCoordinates shiftPoint = new MinecraftCoordinates(0,Parameters.parameters.integerParameter("minecraftEmptySpaceBufferY"),0);
			MinecraftCoordinates oldCorner = evaluationCorner;
			evaluationCorner = evaluationCorner.add(shiftPoint); // move sufficiently above the ground
			shapeCorner = shapeCorner.add(shiftPoint);			//shifts the shape corner as well
			originalBlocks = MinecraftUtilClass.shiftBlocksBetweenCorners(originalBlocks, oldCorner, evaluationCorner);
		}
		//this is the max coordinates of the evaluation space (I think) for calculation the total evaluation area 
		MinecraftCoordinates endEvaluationCorner = evaluationCorner.add(MinecraftUtilClass.reservedSpace());

		//min corner y <= end (max corner y)
		assert evaluationCorner.x() <= endEvaluationCorner.x() && evaluationCorner.y() <= endEvaluationCorner.y() && evaluationCorner.z() <= endEvaluationCorner.z(): "corner should be less than end in each coordinate: corner = "+evaluationCorner+ ", max = "+endEvaluationCorner; 

		if(CommonConstants.watch) System.out.println("Original Blocks: "+originalBlocks);
		if(CommonConstants.watch) System.out.println("Evaluate at corner: "+evaluationCorner);

		//clear and verify evaluation space
		if(Parameters.parameters.booleanParameter("minecraftClearAndVerify")) {
			MinecraftClient.clearAndVerify(shapeCorner);
		}

	////////	creating history 	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//history is a list of time stamps with an associated list of blocks read at that time
		//time stamps calculated as the current time - time that the shape is spawned (startTime)
		ArrayList<Pair<Long,List<Block>>> history = new ArrayList<>();

		//this compares the original blocks with the previous blocks 
		List<Block> previousBlocks = MinecraftUtilClass.wipeOrientations(originalBlocks);
		history.add(new Pair<Long,List<Block>>(-1L,originalBlocks));
		history.add(new Pair<Long,List<Block>>(0L,previousBlocks));

		boolean stop = false;
		List<Block> newShapeReadingBlockList = null;

		if(CommonConstants.watch) 
			System.out.println("Evaluate Blocks: " + originalBlocks);
	//////// Spawn the blocks!	////////////////////////////////////////////////////////////////////////////////////////////////
		
		for(TimedEvaluationMinecraftFitnessFunction ff : fitnessFunctions) {
			// Each fitness function can prepare the space as needed. Not many fitness functions
			// use this. Need to pay attention to maintaining compatibility as more are added.
			ff.preSpawnSetup(shapeCorner);
		}
		
		MinecraftClient.getMinecraftClient().spawnBlocks(originalBlocks);

		long timeBetweenRead = Parameters.parameters.longParameter("shortTimeBetweenMinecraftReads");
		long startTime = System.currentTimeMillis();

		// Stores results from early termination conditions
		Double[] earlyResults = new Double[fitnessFunctions.size()]; // all null

	////////// Wait time and log new reading //////////////////////////////////////////////////////////////////
		while(!stop) {
			try {
				Thread.sleep(timeBetweenRead);
			} catch (InterruptedException e) {
				System.out.print("Thread was interrupted");
				e.printStackTrace();
				System.exit(1);
			}
			// Read the blocks in the evaluation area and remove air blocks
			newShapeReadingBlockList = MinecraftUtilClass.filterOutBlock(MinecraftClient.getMinecraftClient().readCube(evaluationCorner,endEvaluationCorner),BlockType.AIR);
			history.add(new Pair<Long,List<Block>>(System.currentTimeMillis() - startTime,newShapeReadingBlockList));
			if(CommonConstants.watch) System.out.println("Block update: "+newShapeReadingBlockList);

			int index = 0;
			boolean allFitnessFunctionsReturnEarlyResult = true; // Assume early termination will happen
			for(TimedEvaluationMinecraftFitnessFunction ff : fitnessFunctions) {
				// See if this fitness function wants to end early
				if(earlyResults[index] == null) // Only check if it didn't already end early
					earlyResults[index] = ff.earlyEvaluationTerminationResult(shapeCorner, originalBlocks, history, newShapeReadingBlockList);
				// If it still does not want to end early
				if(earlyResults[index] == null)
					allFitnessFunctionsReturnEarlyResult = false; // Then evaluation must continue
				
				index++;
			}
			// Every fitness function wants to terminate early
//			if(CommonConstants.watch) System.out.println("all fitness functions returned RESULTS: "+allFitnessFunctionsReturnEarlyResult);

			// Exit the loop to end evaluation. finalResults will fill from earlyResults below.
			if(allFitnessFunctionsReturnEarlyResult) break; // return ArrayUtil.doubleArrayFromList(Arrays.asList(earlyResults));
			
			// If enough time has elapsed since the start, then end the evaluation
			if(System.currentTimeMillis() - startTime > Parameters.parameters.longParameter("minecraftMandatoryWaitTime")) {
				System.out.println("Time elapsed: minecraftMandatoryWaitTime = "+ Parameters.parameters.longParameter("minecraftMandatoryWaitTime"));
				//if(CommonConstants.watch) System.out.println("Time elapsed: minecraftMandatoryWaitTime = "+ Parameters.parameters.longParameter("minecraftMandatoryWaitTime"));
				stop = true;
			}

		}

		// Do not modify contents of any parameters.
		double[] finalResults = new double[fitnessFunctions.size()];
		for(int i = 0; i < finalResults.length; i++) {
			if(earlyResults[i] != null) { // If this function wanted to end early, then give the early result
				finalResults[i] = earlyResults[i];
			} else { // otherwise, compute the normal final result
				finalResults[i] = fitnessFunctions.get(i).calculateFinalScore(history, shapeCorner, originalBlocks);
			}
			
			if(CommonConstants.netio) { // Is output being saved at all?
				// Does this particular fitness function think the shape is worth saving?
				if(fitnessFunctions.get(i).shapeIsWorthSaving(finalResults[i],history, shapeCorner, originalBlocks)) {
					String saveDir = FileUtilities.getSaveDirectory() + "/" + fitnessFunctions.get(i).getClass().getSimpleName();
					File dir = new File(saveDir);
					// Create dir	-is this create directory or creating a text file?
					if (!dir.exists()) {
						dir.mkdir();
					}
					// Save the shape
					MinecraftUtilClass.writeBlockListFile(originalBlocks, saveDir + File.separator + "Shape"+(++savedShapes), "FITNESS_"+finalResults[i]+".txt");
				}
			}
		}
		
		if(Parameters.parameters.booleanParameter("minecraftClearAfterEvaluation")) MinecraftClient.clearAndVerify(shapeCorner);
		
		return finalResults;
	}

	/**
	 * Given the shape's final score and all the information used to compute the fitness, decide whether it
	 * should be saved to disk for future examination.
	 * 
	 * @param fitnessScore Calculated fitness (could result from early termination)
	 * @param history Periodic snapshots of shape taken during evaluation
	 * @param shapeCorner Corner the shape was spawned at
	 * @param originalBlocks Blocks before instantiation in the world
	 * @return true if the shape should be saved, false otherwise
	 */
	public boolean shapeIsWorthSaving(double fitnessScore, ArrayList<Pair<Long, List<Block>>> history, MinecraftCoordinates shapeCorner, List<Block> originalBlocks) {
		// By default, assume shapes are not worth saving, but this method can be overridden in child classes
		return false;
	}

	/**
	 * Any code that has to run right before the shape is spawned for evaluation
	 * @param corner Corner of shape to evaluate in the world
	 */
	public void preSpawnSetup(MinecraftCoordinates corner) {
		// Do nothing by default
	}

	/**
	 * If there are situations where evaluation should end early, this method detects them and computes
	 * the resulting fitness. However, the default assumption is that evaluation will not end early, which
	 * is what the return result of null represents. This method needs to be overridden in descendants
	 * in order to detect early termination and determine the result.
	 * 
	 * @param corner Minimal coordinate where a block from the shape can be placed
	 * @param originalBlocks the shape being evaluated, before simulation (also first in history, here for convenience)
	 * @param history history of block readings from the world throughout simulation 
	 *        (index 0 is same as originalBlocks, final index same as newShapeReadingBlockList)
	 * @param newShapeBlockList the latest block shape reading (also last in history, here for convenience)
	 * @return null if evaluation should not end early, or a Double fitness value otherwise
	 */
	public Double earlyEvaluationTerminationResult(MinecraftCoordinates corner, List<Block> originalBlocks,
			ArrayList<Pair<Long, List<Block>>> history, List<Block> newShapeBlockList) {
		return null;
	}

	/**
	 * based on history of readings taken of shape, compute final numeric fitness score
	 * @author Joanna Blatt Lewis
	 * @param history the history of block readings during timed evaluation period, contains list of time stamps with associated block list
	 * @param corner the corner that the shape uses
	 * @param originalBlocks the original list of blocks for the shape
	 * @return final fitness score based on other classes evaluation readings
	 */
	public abstract double calculateFinalScore (ArrayList<Pair<Long,List<Block>>> history, MinecraftCoordinates corner, List<Block> originalBlocks);

	/**
	 * Based on the evaluation time and the time between reads, how many readings will be taken?
	 * @return Number of readings taken of the evaluated shape
	 */
	protected long minNumberOfShapeReadings() {
		return Parameters.parameters.longParameter("minecraftMandatoryWaitTime")/Parameters.parameters.longParameter("shortTimeBetweenMinecraftReads");
	}
	
	public static void main(String[] args) {
		try {   
			MMNEAT.main("runNumber:1 randomSeed:1 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftEmptySpaceBufferX:27 minecraftEmptySpaceBufferZ:27 minecraftEmptySpaceBufferY:18 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.DirectRepresentationShapeGenerator trials:1 minecraftMandatoryWaitTime:15000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.ExplosiveBlockSet io:true netio:true interactWithMapElitesInWorld:false mating:true spaceBetweenMinecraftShapes:30 launchMinecraftServerFromJava:false task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.tasks.evocraft.genotype.MinecraftShapeGenotype vectorPresenceThresholdForEachBlock:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearWithGlass:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true rememberParentScores:true extraSpaceBetweenMinecraftShapes:100 mu:100 maxGens:100000 experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5 minecraftAccumulateChangeInCenterOfMass:true ea:edu.southwestern.evolution.mome.MOME maximumMOMESubPopulationSize:10 minecraftCompassMissileTargets:true minecraftTargetDistancefromShapeX:30 minecraftTargetDistancefromShapeY:0 minecraftTargetDistancefromShapeZ:0 minecraftChangeCenterOfMassFitness:true minecraftMissileFitness:true base:minecraftcomplex log:MinecraftComplex-MissileMOME saveTo:MissileMOME".split(" ")); 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
