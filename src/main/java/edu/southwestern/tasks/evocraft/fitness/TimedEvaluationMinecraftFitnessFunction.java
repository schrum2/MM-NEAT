package edu.southwestern.tasks.evocraft.fitness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;

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
		
		//if statement checks if the evaluation space plus the space that would be cleared is below the ground level
		if(evaluationCorner.y() - MinecraftClient.EMPTY_SPACE_SAFETY_BUFFER <= MinecraftClient.GROUND_LEVEL) { // Push up if close to ground
			System.out.println("Pushed up from " + evaluationCorner);
			MinecraftCoordinates shiftPoint = new MinecraftCoordinates(0,MinecraftClient.EMPTY_SPACE_SAFETY_BUFFER,0);
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
		MinecraftClient.clearAndVerify(shapeCorner);

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

		if(CommonConstants.watch) System.out.println("Evaluate Blocks: " + originalBlocks);
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
			boolean allNonNull = true; // Assume early termination will happen
			for(TimedEvaluationMinecraftFitnessFunction ff : fitnessFunctions) {
				// See if this fitness function wants to end early
				if(earlyResults[index] == null) // Only check if it didn't already end early
					earlyResults[index] = ff.earlyEvaluationTerminationResult(shapeCorner, originalBlocks, history, newShapeReadingBlockList);
				// If it still does not want to end early
				if(earlyResults[index] == null)
					allNonNull = false; // Then evaluation must continue
				
				index++;
			}
			// Every fitness function wants to terminate early
			if(allNonNull) return ArrayUtil.doubleArrayFromList(Arrays.asList(earlyResults));
			
			// If enough time has elapsed since the start, then end the evaluation
			if(System.currentTimeMillis() - startTime > Parameters.parameters.longParameter("minecraftMandatoryWaitTime")) {
				System.out.println("Time elapsed: minecraftMandatoryWaitTime = "+ Parameters.parameters.longParameter("minecraftMandatoryWaitTime"));
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
		}
		return finalResults;
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
}
