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
	
	// Flying machines that leave blocks behind get a small fitness penalty proportional to the number of remaining blocks,
	// but scaled down to 10% of that.
	private static final double REMAINING_BLOCK_PUNISHMENT_SCALE = 0.1;
		
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
		return getCenterOfMassBeforeAndAfter(corner, originalBlocks, 0);
	}

	/**
	 * Calculates the initial and final center of mass after
	 * a certain amount of time has passed
	 * 
	 * TODO: Change this name
	 * 
	 * @param corner Coordinate of the corner of the shape
	 * @param originalBlocks Blocks from ShapeGenerator, not from Minecraft
	 * @param How many attempts have been made so far 
	 * @return total change in distance
	 */
	private double getCenterOfMassBeforeAndAfter(MinecraftCoordinates corner, List<Block> originalBlocks, int attempt) {
		attempt++; // For the current attempt
		
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
		if(corner.y() - MinecraftClient.EMPTY_SPACE_SAFETY_BUFFER <= MinecraftClient.GROUND_LEVEL) { // Push up if close to ground
			MinecraftCoordinates shiftPoint = new MinecraftCoordinates(0,MinecraftClient.EMPTY_SPACE_SAFETY_BUFFER,0);
			MinecraftCoordinates oldCorner = corner;
			corner = corner.add(shiftPoint); // move sufficiently above the ground
			originalBlocks = MinecraftUtilClass.shiftBlocksBetweenCorners(originalBlocks, oldCorner, corner);
		}
		MinecraftCoordinates end = corner.add(MinecraftUtilClass.reservedSpace());

		assert corner.x() <= end.x() && corner.y() <= end.y() && corner.z() <= end.z(): "corner should be less than end in each coordinate: corner = "+corner+ ", max = "+end; 

		if(CommonConstants.watch) System.out.println("Original Blocks: "+originalBlocks);
		if(CommonConstants.watch) System.out.println("Evaluate at corner: "+corner);
		
		// Must be clear before starting
		//TODO: maybe move this to minecraftClient, was made in clear and verify
		boolean empty = false;
		int clearAttempt = 0;
		do {
			MinecraftClient.clearAreaAroundCorner(corner, true);
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
			return minFitness();
		}

		// Initial center of mass is where it starts
		Vertex initialCenterOfMass = getCenterOfMass(originalBlocks);
		Vertex lastCenterOfMass = new Vertex(initialCenterOfMass); // Copy constructor (not a copy of reference)
		if(CommonConstants.watch) System.out.println(System.currentTimeMillis()+": Initial center of mass: " + initialCenterOfMass);
		
		boolean stop = false;
		List<Block> shortWaitTimeUpdate = null;
		
		if(CommonConstants.watch) System.out.println(originalBlocks);
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
				return maxFitness();
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
					return result.t3;
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
		if(result != null) return result.t3;
		
		// Machine did not fly away
		Triple<Vertex,Vertex,Double> centerOfMassBeforeAndAfter = new Triple<>(initialCenterOfMass, lastCenterOfMass, totalChangeDistance);
		
		double changeInPosition = centerOfMassBeforeAndAfter.t2.distance(centerOfMassBeforeAndAfter.t1);
		assert !Double.isNaN(changeInPosition) : "Before: " + originalBlocks;

		if(!Parameters.parameters.booleanParameter("minecraftAccumulateChangeInCenterOfMass")) {
			centerOfMassBeforeAndAfter.t3 = changeInPosition;		
		}
		if(CommonConstants.watch) System.out.println("Final result "+centerOfMassBeforeAndAfter);
		return centerOfMassBeforeAndAfter.t3;
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
