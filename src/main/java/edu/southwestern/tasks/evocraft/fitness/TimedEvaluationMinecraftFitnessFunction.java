package edu.southwestern.tasks.evocraft.fitness;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
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

	/**
	 * currently clears blocks around a corner
	 * creates a history: a list of time stamps with an associated list of blocks read at that time
	 * reads until MinecraftMandatoryWaitTime is reached
	 * returns a call to calculateFinalFitnessScore using history, corner, originalBlocks
	 * 
	 * initial corner is the bottom corner of the shape
	 * sub empty offset to find bottom  0-5 = -5 for lower corner of evaluation shape
	 * adds end? to get the far up corner to calculate volume
	 * @param corner the corner of the shape
	 */
	@Override
	public double fitnessScore(MinecraftCoordinates corner, List<Block> originalBlocks) {

		// Should this be true for all fitness functions?
		if(originalBlocks.isEmpty()) {
			if(CommonConstants.watch) System.out.println("Empty shape: Immediate failure");
			return minFitness();
		}		
		
	///////////// clear section - should be reworked and made into utils class ////////////////////////////////////////////////////////////////////

		// Ranges before the change of space in between
		int xrange = Parameters.parameters.integerParameter("minecraftXRange");
		//int yrange = Parameters.parameters.integerParameter("minecraftYRange");
		int zrange = Parameters.parameters.integerParameter("minecraftZRange");

		assert xrange > 0 : "xrange must be positive: " + xrange;
		assert zrange > 0 : "zrange must be positive: " + zrange;

		// Shifts over the corner to the new range with the large space in between shapes
		corner = corner.sub(MinecraftUtilClass.emptySpaceOffsets());
		// schrum2: I think this code is responsible for the weird error of shapes near the ground being stacked vertically.
		//          When the startY is made large enough, this is not an issue, but makin gthe user set that correctly
		//          is a hassle.		
		
		//finds the corner of the evaluation space - corner now means evaluation space
		//if statement checks if the evaluation space plus the space that would be cleared is below the ground level
		if(corner.y() - MinecraftClient.EMPTY_SPACE_SAFETY_BUFFER <= MinecraftClient.GROUND_LEVEL) { // Push up if close to ground
			MinecraftCoordinates shiftPoint = new MinecraftCoordinates(0,MinecraftClient.EMPTY_SPACE_SAFETY_BUFFER,0);
			MinecraftCoordinates oldCorner = corner;
			corner = corner.add(shiftPoint); // move sufficiently above the ground
			originalBlocks = MinecraftUtilClass.shiftBlocksBetweenCorners(originalBlocks, oldCorner, corner);
		}
		//this is the max coordinates of the evaluation space (I think) for calculation the total evaluation area 
		MinecraftCoordinates end = corner.add(MinecraftUtilClass.reservedSpace());

		//min corner y <= end (max corner y)
		assert corner.x() <= end.x() && corner.y() <= end.y() && corner.z() <= end.z(): "corner should be less than end in each coordinate: corner = "+corner+ ", max = "+end; 

		if(CommonConstants.watch) System.out.println("Original Blocks: "+originalBlocks);
		if(CommonConstants.watch) System.out.println("Evaluate at corner: "+corner);

		//clear and verify evaluation space
		MinecraftClient.clearAndVerify(corner);

	////////	creating history 	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//history is a list of time stamps with an associated list of blocks read at that time
		//time stamps calculated as the current time - time that the shape is spawned (startTime)
		ArrayList<Pair<Long,List<Block>>> history = new ArrayList<>();

		//this compares the original blocks with the 
		List<Block> previousBlocks = MinecraftUtilClass.wipeOrientations(originalBlocks);
		history.add(new Pair<Long,List<Block>>(-1L,originalBlocks));
		history.add(new Pair<Long,List<Block>>(0L,previousBlocks));

		boolean stop = false;
		List<Block> newShapeReadingBlockList = null;

		System.out.println(originalBlocks);
	//////// Spawn the blocks!	////////////////////////////////////////////////////////////////////////////////////////////////
		MinecraftClient.getMinecraftClient().spawnBlocks(originalBlocks);

		long timeBetweenRead = Parameters.parameters.longParameter("shortTimeBetweenMinecraftReads");
		long startTime = System.currentTimeMillis();
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
			newShapeReadingBlockList = MinecraftUtilClass.filterOutBlock(MinecraftClient.getMinecraftClient().readCube(corner,end),BlockType.AIR);
			history.add(new Pair<Long,List<Block>>(System.currentTimeMillis() - startTime,newShapeReadingBlockList));
			if(CommonConstants.watch) System.out.println("Block update: "+newShapeReadingBlockList);

			// A non-null result should be returned, and end evaluation early. Otherwise, keep evaluating.
			Double earlyResult = earlyEvaluationTerminationResult(corner, originalBlocks, history, newShapeReadingBlockList);
			if(earlyResult != null) return earlyResult;
			
			// If enough time has elapsed since the start, then end the evaluation
			if(System.currentTimeMillis() - startTime > Parameters.parameters.longParameter("minecraftMandatoryWaitTime")) {
				System.out.println("Time elapsed: minecraftMandatoryWaitTime = "+ Parameters.parameters.longParameter("minecraftMandatoryWaitTime"));
				stop = true;
			}

		}

		//return result of a method call to calculateFinalScore
		return calculateFinalScore(history, corner, originalBlocks);
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
	 * @param newShapeReadingBlockList the latest block shape reading (also last in history, here for convenience)
	 * @return null if evaluation should not end early, or a Double fitness value otherwise
	 */
	public Double earlyEvaluationTerminationResult(MinecraftCoordinates corner, List<Block> originalBlocks,
			ArrayList<Pair<Long, List<Block>>> history, List<Block> newShapeReadingBlockList) {
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

}
