package edu.southwestern.tasks.evocraft.fitness;

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
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.util.datastructures.Triple;
import edu.southwestern.util.datastructures.Vertex;
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
	// Flying machines that leave blocks behind get a small fitness penalty proportional to the number of remaining blocks,
	// but scaled down to 10% of that.
	private static final double REMAINING_BLOCK_PUNISHMENT_SCALE = 0.1;
	private static final HashMap<MinecraftCoordinates, Triple<Vertex, Vertex, Double>> PREVIOUSLY_COMPUTED_RESULTS = new HashMap<>();
	
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
	public double fitnessScore(MinecraftCoordinates corner) {

		Triple<Vertex, Vertex, Double> centerOfMassBeforeAndAfter = getCenterOfMassBeforeAndAfter(corner);
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
	 * @return A triple with the initial center of mass, final center of mass, and total
	 * 			change in distance
	 */
	public Triple<Vertex, Vertex, Double> getCenterOfMassBeforeAndAfter(MinecraftCoordinates corner) {
		// Ranges before the change of space in between
		int xrange = Parameters.parameters.integerParameter("minecraftXRange");
		//int yrange = Parameters.parameters.integerParameter("minecraftYRange");
		int zrange = Parameters.parameters.integerParameter("minecraftZRange");

		assert xrange > 0 : "xrange must be positive: " + xrange;
		assert zrange > 0 : "zrange must be positive: " + zrange;

		// Shifts over the corner to the new range with the large space in between shapes
		corner = corner.sub(MinecraftUtilClass.emptySpaceOffsets());
		MinecraftCoordinates end = corner.add(MinecraftUtilClass.reservedSpace());

		assert corner.x() <= end.x() && corner.y() <= end.y() && corner.z() <= end.z(): "corner should be less than end in each coordinate: corner = "+corner+ ", max = "+end; 

		if(CommonConstants.watch) System.out.println("Evaluate at corner: "+corner);
		//		System.out.println("end:"+end);

		double totalChangeDistance = 0.0;

		// List of blocks in the area based on the corner
		List<Block> blocks = MinecraftClient.getMinecraftClient().readCube(corner,end);
		blocks = MinecraftUtilClass.filterOutBlock(blocks, BlockType.AIR);
		// Initial count cannot include extended pistons since that means the count might decrease even though shape has not flown away.
		List<Block> originalBlocks = MinecraftUtilClass.filterOutBlock(MinecraftUtilClass.filterOutBlock(blocks, BlockType.PISTON_HEAD),BlockType.PISTON_EXTENSION);
		List<Block> previousBlocks = originalBlocks;
		int initialBlockCount = originalBlocks.size();
		if(blocks.isEmpty()) {
			if(CommonConstants.watch) System.out.println("Empty shape: Immediate failure");
			return new Triple<>(new Vertex(0,0,0), new Vertex(0,0,0), minFitness());
		}

		//System.out.println("List of blocks before movement: "+ Arrays.toString(blocks.stream().filter(b -> b.type() != BlockType.AIR.ordinal()).toArray()));

		// Initial center of mass is where it starts
		Vertex initialCenterOfMass = getCenterOfMass(blocks);
		Vertex lastCenterOfMass = new Vertex(initialCenterOfMass); // Copy constructor (not a copy of reference)
		if(CommonConstants.watch) System.out.println(System.currentTimeMillis()+": Initial center of mass: " + initialCenterOfMass);
		//System.out.println("total change vertex: " + totalChangeVertex);
		//System.out.println(initialCenterOfMass);

		boolean stop = false;
		
		List<Block> shortWaitTimeUpdate = new ArrayList<>();
		
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
				// It is possible the shape flew away leaving some stationary parts
				List<Block> updatedBlocksWithoutExtendedPistons = MinecraftUtilClass.filterOutBlock(MinecraftUtilClass.filterOutBlock(shortWaitTimeUpdate, BlockType.PISTON_HEAD),BlockType.PISTON_EXTENSION);
				int remainingBlockCount = updatedBlocksWithoutExtendedPistons.size();
				int departedBlockCount = initialBlockCount - remainingBlockCount;
				// At least half of the blocks need to leave before we consider the shape to be flying
				if(departedBlockCount >= initialBlockCount/2) {
					if(CommonConstants.watch) System.out.println("Enough have departed. departedBlockCount is "+departedBlockCount+ " from initialBlockCount of "+initialBlockCount);					
//					assert false : "remainingBlockCount = "+remainingBlockCount+"\ninitialBlockCount = "+initialBlockCount+"\ndepartedBlockCount = "+departedBlockCount+
//						"\nshortWaitTimeUpdate                = "+shortWaitTimeUpdate+
//						"\nblocks                             = "+blocks+
//						"\noriginalBlocks                     = "+originalBlocks+
//						"\nupdatedBlocksWithoutExtendedPistons= "+updatedBlocksWithoutExtendedPistons+
//						"\ninitialCenterOfMass = "+initialCenterOfMass+"\nnextCenterOfMass = "+nextCenterOfMass+
//						"\ncorner = "+corner;
					
					
					// Ship flew so far away that we award max fitness, but penalize remaining blocks
					System.out.println(remainingBlockCount +" remaining blocks: max = " + maxFitness());
					return new Triple<>(initialCenterOfMass, lastCenterOfMass, maxFitness() - remainingBlockCount*REMAINING_BLOCK_PUNISHMENT_SCALE);
				}
				
				stop = true;
			} else {
				totalChangeDistance += lastCenterOfMass.distance(nextCenterOfMass);
				if(CommonConstants.watch) System.out.println("Total is now: "+totalChangeDistance);
				lastCenterOfMass = nextCenterOfMass;
				previousBlocks = shortWaitTimeUpdate; // Remember the previous block list
				if(System.currentTimeMillis() - startTime > Parameters.parameters.longParameter("minecraftMandatoryWaitTime")) {
					System.out.println("Time elapsed: minecraftMandatoryWaitTime = "+ Parameters.parameters.longParameter("minecraftMandatoryWaitTime"));
					stop = true;
				}
			}
		}

		Triple<Vertex,Vertex,Double> centerOfMassBeforeAndAfter = new Triple<>(initialCenterOfMass, lastCenterOfMass, totalChangeDistance);
		
		double changeInPosition = centerOfMassBeforeAndAfter.t2.distance(centerOfMassBeforeAndAfter.t1);
		assert !Double.isNaN(changeInPosition) : "Before: " + MinecraftUtilClass.filterOutBlock(blocks,BlockType.AIR);

		if(!Parameters.parameters.booleanParameter("minecraftAccumulateChangeInCenterOfMass")) {
			centerOfMassBeforeAndAfter.t3 = changeInPosition;		
		}
		if(CommonConstants.watch) System.out.println("Final result "+centerOfMassBeforeAndAfter);
		return centerOfMassBeforeAndAfter;
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
			MMNEAT.main(new String[] { "runNumber:1","randomSeed:1",
					"base:minecraftaccumulate","log:MinecraftAccumulate-VectorCountNegativeUpDown","saveTo:VectorCountNegativeUpDown",
					"mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBlockCountEmptyCountBinLabels",
					"minecraftXRange:2","minecraftYRange:4","minecraftZRange:3",
					"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator",
					"minecraftChangeCenterOfMassFitness:true",
					"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet",
					"trials:1","mu:100","maxGens:100000",
					"minecraftContainsWholeMAPElitesArchive:true","forceLinearArchiveLayoutInMinecraft:false",
					"launchMinecraftServerFromJava:false","io:true","netio:true",
					"interactWithMapElitesInWorld:true","mating:true","fs:false",
					"ea:edu.southwestern.evolution.mapelites.MAPElites",
					"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
					"steadyStateIndividualsPerGeneration:100",
					"spaceBetweenMinecraftShapes:5",
					"task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask",
					"watch:false","saveAllChampions:true",
					"genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype",
					"vectorPresenceThresholdForEachBlock:true",
					"voxelExpressionThreshold:0.5",
					"minecraftAccumulateChangeInCenterOfMass:true","minecraftUpDownOnly:true",
					"parallelEvaluations:true","threads:10","parallelMAPElitesInitialize:true"}); 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
