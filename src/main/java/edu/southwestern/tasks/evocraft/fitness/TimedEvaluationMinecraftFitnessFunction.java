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

public abstract class TimedEvaluationMinecraftFitnessFunction extends MinecraftFitnessFunction{
	//constructor
	//start with clearing


	//fitnessScore clear space around the designated corner
	//next spawn shape
	//wait for amount of time

	//TODO: all public things in changeCenterOfMass being called should be made into util class

	public double fitnessScore(MinecraftCoordinates corner, List<Block> originalBlocks) {
		//clear space (module out?)

		// Ranges before the change of space in between
		int xrange = Parameters.parameters.integerParameter("minecraftXRange");
		//int yrange = Parameters.parameters.integerParameter("minecraftYRange");
		int zrange = Parameters.parameters.integerParameter("minecraftZRange");

		assert xrange > 0 : "xrange must be positive: " + xrange;
		assert zrange > 0 : "zrange must be positive: " + zrange;

		// Shifts over the corner to the new range with the large space in between shapes
		corner = corner.sub(MinecraftUtilClass.emptySpaceOffsets());
		if(corner.y() - ChangeCenterOfMassFitness.SPECIAL_CORNER_BUFFER <= MinecraftClient.GROUND_LEVEL) { // Push up if close to ground
			MinecraftCoordinates shiftPoint = new MinecraftCoordinates(0,ChangeCenterOfMassFitness.SPECIAL_CORNER_BUFFER,0);
			MinecraftCoordinates oldCorner = corner;
			corner = corner.add(shiftPoint); // move sufficiently above the ground
			originalBlocks = MinecraftUtilClass.shiftBlocksBetweenCorners(originalBlocks, oldCorner, corner);
		}
		MinecraftCoordinates end = corner.add(MinecraftUtilClass.reservedSpace());

		assert corner.x() <= end.x() && corner.y() <= end.y() && corner.z() <= end.z(): "corner should be less than end in each coordinate: corner = "+corner+ ", max = "+end; 

		if(CommonConstants.watch) System.out.println("Original Blocks: "+originalBlocks);
		if(CommonConstants.watch) System.out.println("Evaluate at corner: "+corner);

		// Must be clear before starting
		boolean empty = false;
		int clearAttempt = 0;
		do {
			ChangeCenterOfMassFitness.clearAreaAroundCorner(corner);
			empty = ChangeCenterOfMassFitness.areaAroundCornerEmpty(corner);
			if(!empty) System.out.println("Cleared "+(++clearAttempt)+" times: empty?: "+empty);
		} while(!empty);

		ArrayList<List<Block>> history = new ArrayList<>();

		List<Block> previousBlocks = MinecraftUtilClass.wipeOrientations(originalBlocks);
		history.add(originalBlocks);
		history.add(previousBlocks);

		if(originalBlocks.isEmpty()) {
			if(CommonConstants.watch) System.out.println("Empty shape: Immediate failure");
			return minFitness();
		}


		boolean stop = false;
		List<Block> shortWaitTimeUpdate = null;

		System.out.println(originalBlocks);
		// Spawn the blocks!
		MinecraftClient.getMinecraftClient().spawnBlocks(originalBlocks);

		long shortWaitTime = Parameters.parameters.longParameter("shortTimeBetweenMinecraftReads");
		long startTime = System.currentTimeMillis();
		// Wait for the machine to move some (if at all)
		while(!stop) {
			try {
				Thread.sleep(shortWaitTime);
			} catch (InterruptedException e) {
				System.out.print("Thread was interrupted");
				e.printStackTrace();
				System.exit(1);
			}
			shortWaitTimeUpdate = MinecraftUtilClass.filterOutBlock(MinecraftClient.getMinecraftClient().readCube(corner,end),BlockType.AIR);
			history.add(shortWaitTimeUpdate);
			if(CommonConstants.watch) System.out.println("Block update: "+shortWaitTimeUpdate);


			if(System.currentTimeMillis() - startTime > Parameters.parameters.longParameter("minecraftMandatoryWaitTime")) {
				System.out.println("Time elapsed: minecraftMandatoryWaitTime = "+ Parameters.parameters.longParameter("minecraftMandatoryWaitTime"));
				stop = true;
			}

		}

		//spawn shape (module out?)

		//wait

		//return result of a method call to calculateFinalScore
		return calculateFinalScore(history, corner, originalBlocks);

		//
	}

	//return result of a method call to calculateFinalScore
	
	/**
	 * based on history of readings taken of shape, compute final numeric fitness score
	 * @author Joanna Blatt Lewis
	 * @param history
	 * @param corner
	 * @param originalBlocks
	 * @return
	 */
	public abstract double calculateFinalScore (ArrayList<List<Block>> history, MinecraftCoordinates corner, List<Block> originalBlocks);

}
