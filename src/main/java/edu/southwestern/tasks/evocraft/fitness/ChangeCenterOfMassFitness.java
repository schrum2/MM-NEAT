package edu.southwestern.tasks.evocraft.fitness;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.util.datastructures.Triple;
import edu.southwestern.util.datastructures.Vertex;
import edu.southwestern.util.file.FileUtilities;
/**
 * Calculates the changes in the center of mass of
 * a given structure. If the structure is a flying machine
 * then it will have a positive non-zero fitness score (which is
 * dependent on the mandatory wait time parameter). Otherwise, the
 * structure is stagnant, meaning it has a fitness of 0.
 * @author Melanie Richey
 *
 */
public class ChangeCenterOfMassFitness extends MinecraftFitnessFunction{
	// Assume that the remaining block penalty will not be greater than this (should actually be much less)
	public static final double FLYING_PENALTY_BUFFER = 5;
	// At least this many blocks must depart to count as flying
	private static final int SUFFICIENT_DEPARTED_BLOCKS = 6;
	// The machine must clearly fly on this many separate evaluations before being awarded such fitness
	private static final int ATTEMPTS_BEFORE_CONVINCED_OF_FLYING = 1; //2;
	
	// Flying machines that leave blocks behind get a small fitness penalty proportional to the number of remaining blocks,
	// but scaled down to 10% of that.
	private static final double REMAINING_BLOCK_PUNISHMENT_SCALE = 0.1;
	private static final HashMap<MinecraftCoordinates, Triple<Vertex, Vertex, Double>> PREVIOUSLY_COMPUTED_RESULTS = new HashMap<>();
	/*
	 * // Nowhere near where anything else is being evaluated public static final
	 * MinecraftCoordinates SPECIAL_CORNER = new MinecraftCoordinates(-500, 100,
	 * 500); public static final int SPECIAL_CORNER_BUFFER = 20;
	 */
	
//	/**
//	 * Make sure the special area for double-checking flying shapes is really clear
//	 */
//	public static void clearAreaAroundSpecialCorner() {
//		clearAreaAroundCorner(SPECIAL_CORNER);
//	}
//	/**
//	 * body of code for for clearAreaAroundSpecialCorner used above
//	 * @param corner
//	 */
//	public static void clearAreaAroundCorner(MinecraftCoordinates corner) {
//		MinecraftCoordinates lower = corner.sub(SPECIAL_CORNER_BUFFER);
//		MinecraftCoordinates upper = corner.add(MinecraftUtilClass.getRanges().add(SPECIAL_CORNER_BUFFER));
//		MinecraftClient.getMinecraftClient().clearCube(lower, upper, BlockType.AIR);
//		List<Block> errorCheck = null;
//		assert areaAroundCornerEmpty(corner) : "Area not empty after clearing! "+errorCheck;
//	}
//	/**
//	 * Checks if the area around a corner is empty
//	 * @param corner
//	 * @return boolean if space is empty or not
//	 */
//	public static boolean areaAroundCornerEmpty(MinecraftCoordinates corner) {
//		MinecraftCoordinates lower = corner.sub(SPECIAL_CORNER_BUFFER);
//		MinecraftCoordinates upper = corner.add(MinecraftUtilClass.getRanges().add(SPECIAL_CORNER_BUFFER));
//		List<Block> errorCheck = MinecraftUtilClass.filterOutBlock(MinecraftClient.getMinecraftClient().readCube(lower, upper), BlockType.AIR);
////		if(!errorCheck.isEmpty()) {
////			System.out.println("NOT EMPTY at corner "+corner+"\n"+errorCheck);
////			MiscUtil.waitForReadStringAndEnterKeyPress();
////		}
//		return errorCheck.isEmpty();
//	}
	/**
	 * clears previous results
	 */
	public static void resetPreviousResults() {
		PREVIOUSLY_COMPUTED_RESULTS.clear();
	}
	
	/**
	 * Retrieve and remove a previously computed result
	 * @param corner Corner where the shape was evaluated
	 * @return Results for shape that was spawned at that corner
	 */
	public static Triple<Vertex, Vertex, Double> getPreviouslyComputedResult(MinecraftCoordinates corner) {
		synchronized(PREVIOUSLY_COMPUTED_RESULTS) {
			return PREVIOUSLY_COMPUTED_RESULTS.remove(corner);
		}
	}

	@Override
	public double maxFitness() {
		// Probably overshoots a bit
		if(Parameters.parameters.booleanParameter("minecraftAccumulateChangeInCenterOfMass")) return ((Parameters.parameters.longParameter("minecraftMandatoryWaitTime")/Parameters.parameters.longParameter("shortTimeBetweenMinecraftReads")) + 1) * overestimatedDistanceToEdge();
		else return overestimatedDistanceToEdge();
	}
	
	/**
	 * About the distance from the center of the area the shape is generated in to
	 * the edge of the space the shape is generated in.
	 * 
	 * @return Overestimate of distance from center to edge
	 */
	public double overestimatedDistanceToEdge() {
		double oneDir = Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes") + Math.max(Math.max(Parameters.parameters.integerParameter("minecraftXRange"), Parameters.parameters.integerParameter("minecraftYRange")),Parameters.parameters.integerParameter("minecraftZRange"));
		return oneDir / 2;
	}

	@Override
	public double fitnessScore(MinecraftCoordinates corner, List<Block> originalBlocks) {

		Triple<Vertex, Vertex, Double> centerOfMassBeforeAndAfter = getCenterOfMassBeforeAndAfter(corner, originalBlocks, 0);
		// Do not erase previously computed result. Wait until other thread consumes it
		while(PREVIOUSLY_COMPUTED_RESULTS.containsKey(corner)) {
			try {
				Thread.sleep(10); // Should this sleep longer?
			} catch (InterruptedException e) {
				System.out.println("Interrupted!");
				e.printStackTrace();
			}
		}
		// Save results computed at the given corner
		synchronized(PREVIOUSLY_COMPUTED_RESULTS) {
			PREVIOUSLY_COMPUTED_RESULTS.put(corner, centerOfMassBeforeAndAfter);
		}
		
//		if(centerOfMassBeforeAndAfter.t3 == 454.5) {
//			System.out.println(centerOfMassBeforeAndAfter);
//			System.out.println("corner: " + corner);
//			throw new IllegalArgumentException();
//		}
		return centerOfMassBeforeAndAfter.t3;
	}

	/**
	 * Calculates the initial and final center of mass after
	 * a certain amount of time has passed
	 * @param corner Coordinate of the corner of the shape
	 * @param originalBlocks Blocks from ShapeGenerator, not from Minecraft
	 * @param How many attempts have been made so far 
	 * @return A triple with the initial center of mass, final center of mass, and total
	 * 			change in distance
	 */
	private Triple<Vertex, Vertex, Double> getCenterOfMassBeforeAndAfter(MinecraftCoordinates corner, List<Block> originalBlocks, int attempt) {
		attempt++; // For the current attempt
		
		// Ranges before the change of space in between
		int xrange = Parameters.parameters.integerParameter("minecraftXRange");
		//int yrange = Parameters.parameters.integerParameter("minecraftYRange");
		int zrange = Parameters.parameters.integerParameter("minecraftZRange");

		assert xrange > 0 : "xrange must be positive: " + xrange;
		assert zrange > 0 : "zrange must be positive: " + zrange;

		// Shifts over the corner to the new range with the large space in between shapes
		corner = corner.sub(MinecraftUtilClass.emptySpaceOffsets());
		if(corner.y() - MinecraftClient.SPECIAL_CORNER_BUFFER <= MinecraftClient.GROUND_LEVEL) { // Push up if close to ground
			MinecraftCoordinates shiftPoint = new MinecraftCoordinates(0,MinecraftClient.SPECIAL_CORNER_BUFFER,0);
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
			MinecraftClient.clearAreaAroundCorner(corner);
			empty = MinecraftClient.areaAroundCornerEmpty(corner);
			if(!empty) System.out.println("Cleared "+(++clearAttempt)+" times: empty?: "+empty);
		} while(!empty);

		ArrayList<List<Block>> history = new ArrayList<>();
		
		double totalChangeDistance = 0.0;
		// These blocks will be compared with blocks read from the world, which will have only null orientations
		List<Block> previousBlocks = MinecraftUtilClass.wipeOrientations(originalBlocks);
		history.add(originalBlocks);
		history.add(previousBlocks);
		int initialBlockCount = originalBlocks.size();
		if(originalBlocks.isEmpty()) {
			if(CommonConstants.watch) System.out.println("Empty shape: Immediate failure");
			return new Triple<>(new Vertex(0,0,0), new Vertex(0,0,0), minFitness());
		}

		// Initial center of mass is where it starts
		Vertex initialCenterOfMass = getCenterOfMass(originalBlocks);
		Vertex lastCenterOfMass = new Vertex(initialCenterOfMass); // Copy constructor (not a copy of reference)
		if(CommonConstants.watch) System.out.println(System.currentTimeMillis()+": Initial center of mass: " + initialCenterOfMass);
		
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
			if(shortWaitTimeUpdate.isEmpty()) { // If list is empty now (but was not before) then shape has flown completely away
				if(CommonConstants.watch) System.out.println(System.currentTimeMillis()+": Shape empty now: max fitness! Last center of mass = "+lastCenterOfMass);
				return new Triple<>(initialCenterOfMass, lastCenterOfMass, maxFitness());
			}
			Vertex nextCenterOfMass = getCenterOfMass(shortWaitTimeUpdate);
			if(CommonConstants.watch) System.out.println(System.currentTimeMillis()+": Next COM: "+nextCenterOfMass);
			//System.out.println("Does last equals next? " + lastCenterOfMass + " and " + nextCenterOfMass);
			
			// Only consider the shape to not be moving if the center of mass is the same AND the entire block list is the same
			if(Parameters.parameters.booleanParameter("minecraftEndEvalNoMovement") && lastCenterOfMass.equals(nextCenterOfMass) && previousBlocks.equals(shortWaitTimeUpdate)) {
				// This means that it hasn't moved, so move on to the next.
				// BUT What if it moves back and forth and returned to its original position?
				if(CommonConstants.watch) System.out.println(System.currentTimeMillis()+": No movement.");
				// Compute farthest center of mass from history
				Vertex farthestCenterOfMass = getFarthestCenterOfMass(history, initialCenterOfMass, lastCenterOfMass);
				Triple<Vertex, Vertex, Double> result = checkCreditForDepartedBlocks(initialBlockCount, initialCenterOfMass, farthestCenterOfMass, shortWaitTimeUpdate);
				if(result != null) {
					if(CommonConstants.netio) {
						String flyingDir = FileUtilities.getSaveDirectory() + "/possibleFlyingMachines";
						File dir = new File(flyingDir);
						// Create dir
						if (!dir.exists()) {
							dir.mkdir();
						}
						MinecraftLonerShapeTask.writeBlockListFile(originalBlocks, flyingDir + File.separator + "Attempt"+attempt, "FITNESS_"+result.t3+".txt");
					}
					System.out.println("Flying machine from attempt "+attempt);
					for(int i = 0; i < history.size(); i++) {
						System.out.println(i + "." + history.get(i));
					}
					// Repeat until certain
					if(attempt < ATTEMPTS_BEFORE_CONVINCED_OF_FLYING) {
						System.out.println("Check flying machine again");
						// Only one shape can be evaluated in this place at a time
						synchronized(MinecraftClient.SPECIAL_CORNER) {
							MinecraftClient.clearAreaAroundSpecialCorner();
							List<Block> shiftedBlocks = MinecraftUtilClass.shiftBlocksBetweenCorners(originalBlocks, corner, MinecraftClient.SPECIAL_CORNER);
							MinecraftClient.getMinecraftClient().spawnBlocks(shiftedBlocks);
							result = getCenterOfMassBeforeAndAfter(MinecraftClient.SPECIAL_CORNER, shiftedBlocks, attempt);
						}
					} else {
						System.out.println("Machine succeeded "+ATTEMPTS_BEFORE_CONVINCED_OF_FLYING+" times!");
					}
					
//					for(int i = 0; i < history.size(); i++) {
//						System.out.println(i + "." + history.get(i));
//					}
//					System.out.println("\noriginalBlocks                     = "+originalBlocks+
//							"\ninitialCenterOfMass = "+initialCenterOfMass+"\nnextCenterOfMass = "+nextCenterOfMass+
//							"\ncorner = "+corner+
//							"\nbig area around is "+MinecraftUtilClass.filterOutBlock(MinecraftClient.getMinecraftClient().readCube(corner.sub(new MinecraftCoordinates(5,5,5)),end.add(new MinecraftCoordinates(5,5,5))),BlockType.AIR) );
//						System.exit(1);
					return result;
				}
				
				stop = true;
			} else {
				//if evaluating and rewarding fast flying machines
				if(Parameters.parameters.booleanParameter("minecraftRewardFastFlyingMachines")) {
					//calculates based on initial center of mass and the next center of mass to add to total change distance
					totalChangeDistance += initialCenterOfMass.distance(nextCenterOfMass);
				} else {
					totalChangeDistance += lastCenterOfMass.distance(nextCenterOfMass);
				}
				if(CommonConstants.watch) System.out.println("Total is now: "+totalChangeDistance);
				lastCenterOfMass = nextCenterOfMass;
				previousBlocks = shortWaitTimeUpdate; // Remember the previous block list
				if(System.currentTimeMillis() - startTime > Parameters.parameters.longParameter("minecraftMandatoryWaitTime")) {
					System.out.println("Time elapsed: minecraftMandatoryWaitTime = "+ Parameters.parameters.longParameter("minecraftMandatoryWaitTime"));
					stop = true;
				}
			}
		}
		
		// It is possible that blocks flew away, but some remaining component kept oscillating until the end. This is still a flying machine though.
		Vertex farthestCenterOfMass = getFarthestCenterOfMass(history, initialCenterOfMass, lastCenterOfMass);
		Triple<Vertex, Vertex, Double> result = checkCreditForDepartedBlocks(initialBlockCount, initialCenterOfMass, farthestCenterOfMass, shortWaitTimeUpdate);
		if(result != null) return result;
		
		// Machine did not fly away
		Triple<Vertex,Vertex,Double> centerOfMassBeforeAndAfter = new Triple<>(initialCenterOfMass, lastCenterOfMass, totalChangeDistance);
		
		double changeInPosition = centerOfMassBeforeAndAfter.t2.distance(centerOfMassBeforeAndAfter.t1);
		assert !Double.isNaN(changeInPosition) : "Before: " + originalBlocks;

		if(!Parameters.parameters.booleanParameter("minecraftAccumulateChangeInCenterOfMass")) {
			centerOfMassBeforeAndAfter.t3 = changeInPosition;		
		}
		if(CommonConstants.watch) System.out.println("Final result "+centerOfMassBeforeAndAfter);
		return centerOfMassBeforeAndAfter;
	}

	/**
	 * method that makes sure you are taking the farthestCenterOfMass from history
	 * @param history record of the shape
	 * @param initialCenterOfMass initial center of mass
	 * @param lastCenterOfMass center of mass at the last point
	 * @return center of ,ass that was the farthest away from the initial
	 */
	public Vertex getFarthestCenterOfMass(ArrayList<List<Block>> history, Vertex initialCenterOfMass,
			Vertex lastCenterOfMass) {
		Vertex farthestCenterOfMass = lastCenterOfMass; // Assume last location was farthest
		double farthestDistance = lastCenterOfMass.distance(initialCenterOfMass);
		for(List<Block> blocks : history) {
			Vertex v = getCenterOfMass(blocks);
			double distance = v.distance(initialCenterOfMass);
			if(distance > farthestDistance) {
				farthestDistance = distance;
				farthestCenterOfMass = v;
			}
		}
		return farthestCenterOfMass;
	}
	/**
	 * method that recognizes and punishes flying machines with leftover blocks
	 * @param initialBlockCount block count at origin
	 * @param initialCenterOfMass initial center of mass
	 * @param lastCenterOfMass center of mass at the last point
	 * @param shortWaitTimeUpdate what the blocks look like after a short update
	 * @return fitness after punishment for remaining blocks
	 */
	private Triple<Vertex, Vertex, Double> checkCreditForDepartedBlocks(int initialBlockCount, Vertex initialCenterOfMass, Vertex lastCenterOfMass, List<Block> shortWaitTimeUpdate) {
		int remainingBlockCount = shortWaitTimeUpdate.size(); // Could be larger than initial due to extensions
		int departedBlockCount = initialBlockCount - remainingBlockCount; // Could be negative due to extensions
		Triple<Vertex, Vertex, Double> result = null;
		// It should be hard to archive credit for flying, so make sure that the number of departed blocks is sufficiently high
		if(departedBlockCount > SUFFICIENT_DEPARTED_BLOCKS) {
			if(CommonConstants.watch) System.out.println("Enough have departed. departedBlockCount is "+departedBlockCount+ " from initialBlockCount of "+initialBlockCount);					

//			System.out.println( "remainingBlockCount = "+remainingBlockCount+"\ninitialBlockCount = "+initialBlockCount+"\ndepartedBlockCount = "+departedBlockCount+
//					"\nshortWaitTimeUpdate                = "+shortWaitTimeUpdate );
			
			
			// Ship flew so far away that we award max fitness, but penalize remaining blocks
			System.out.println(remainingBlockCount +" remaining blocks: max = " + maxFitness());
			result = new Triple<>(initialCenterOfMass, lastCenterOfMass, maxFitness() - remainingBlockCount*REMAINING_BLOCK_PUNISHMENT_SCALE);
		}
		return result;
	}

	public static Vertex getCenterOfMass(List<Block> blocks) {
		double x = 0;
		double y = 0;
		double z = 0;

		List<Block> filteredBlocks = MinecraftUtilClass.filterOutBlock(blocks,BlockType.AIR);

		for(Block b : filteredBlocks) {
			x += b.x();
			y += b.y();
			z += b.z();
		}

		double avgX = x/filteredBlocks.size();
		double avgY = y/filteredBlocks.size();
		double avgZ = z/filteredBlocks.size();

		Vertex centerOfMass = new Vertex(avgX,avgY,avgZ);

		return centerOfMass;
	}

	@Override
	public double minFitness() {
		return 0;
	}

	public static void main(String[] args) {
		try {
			MMNEAT.main("runNumber:90 randomSeed:98 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftRewardFastFlyingMachines:true minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftNorthSouthOnly:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:60000 minecraftContainsWholeMAPElitesArchive:true forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:minecraftaccumulate log:MinecraftAccumulate-TESTING saveTo:TESTING mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5".split(" ")); 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
