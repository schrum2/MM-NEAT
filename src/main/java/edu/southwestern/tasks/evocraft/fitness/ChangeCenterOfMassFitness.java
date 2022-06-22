package edu.southwestern.tasks.evocraft.fitness;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
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

		//		System.out.println("corner:"+corner);
		//		System.out.println("end:"+end);

		double totalChangeDistance = 0.0;

		// List of blocks in the area based on the corner
		List<Block> blocks = MinecraftClient.getMinecraftClient().readCube(corner,end);
		blocks = MinecraftUtilClass.filterOutBlock(blocks, BlockType.AIR);
		if(blocks.isEmpty()) return minFitness();

		//System.out.println("List of blocks before movement: "+ Arrays.toString(blocks.stream().filter(b -> b.type() != BlockType.AIR.ordinal()).toArray()));

		// Initial center of mass is where it starts
		Vertex initialCenterOfMass = getCenterOfMass(blocks);
		Vertex lastCenterOfMass = new Vertex(initialCenterOfMass); // Copy constructor (not a copy of reference)
		//System.out.println("Init center of mass: " + initialCenterOfMass);
		//System.out.println("total change vertex: " + totalChangeVertex);
		//System.out.println(initialCenterOfMass);

		boolean stop = false;
		
		List<Block> shortWaitTimeUpdate = new ArrayList<>();
		
		long startTime = System.currentTimeMillis();
		// Wait for the machine to move some (if at all)
		while(!stop) {

			long shortWaitTime = Parameters.parameters.longParameter("shortTimeBetweenMinecraftReads");
			try {
				Thread.sleep(shortWaitTime);
			} catch (InterruptedException e) {
				System.out.print("Thread was interrupted");
				e.printStackTrace();
				System.exit(1);
			}
			// Should we check the actual time? Or is this fine?
//			timeElapsed += shortWaitTime;

			List<Block> previousCheck = shortWaitTimeUpdate;
			shortWaitTimeUpdate = MinecraftUtilClass.filterOutBlock(MinecraftClient.getMinecraftClient().readCube(corner,end),BlockType.AIR);
			//System.out.println("Short wait time Update list: " + shortWaitTimeUpdate);
			if(shortWaitTimeUpdate.isEmpty() || shortWaitTimeUpdate.size() <= Parameters.parameters.integerParameter("leftoverMinecraftBlocksAllowed") || previousCheck.size() > Parameters.parameters.integerParameter("leftoverMinecraftBlocksAllowed")) {
				// Ship flew so far away that we award max fitness
				System.out.println("Where fitness");
				return maxFitness();
			}
			Vertex nextCenterOfMass = getCenterOfMass(shortWaitTimeUpdate);
			//System.out.println("Next COM: "+nextCenterOfMass);
			//System.out.println("Does last equals next? " + lastCenterOfMass + " and " + nextCenterOfMass);
			if(Parameters.parameters.booleanParameter("minecraftEndEvalNoMovement") && lastCenterOfMass.equals(nextCenterOfMass)) {
				// This means that it hasn't moved, so move on to the next.
				// BUT What if it moves back and forth and returned to its original position?
				//System.out.println("Detected no movement");
				//System.out.println("Not moving, next");
				stop = true;
			} else {
				totalChangeDistance += lastCenterOfMass.distance(nextCenterOfMass);
				//System.out.println("After adding: "+totalChangeDistance);
				lastCenterOfMass = nextCenterOfMass;
				if(System.currentTimeMillis() - startTime > Parameters.parameters.longParameter("minecraftMandatoryWaitTime")) {
					//System.out.println("Next structure");
					stop = true;
				}
			}
		}

		double changeInPosition = lastCenterOfMass.distance(initialCenterOfMass);
		assert !Double.isNaN(changeInPosition) : "Before: " + MinecraftUtilClass.filterOutBlock(blocks,BlockType.AIR);
		if(Parameters.parameters.booleanParameter("minecraftAccumulateChangeInCenterOfMass")) return totalChangeDistance;
		else return changeInPosition;
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
		int seed = 1;
		try {
			MMNEAT.main(new String[] { "runNumber:" + seed, "randomSeed:" + seed, "trials:1", "mu:100", "maxGens:100000",
					"base:minecraft", "log:Minecraft-MAPElitesCountNegativeAccumulateChange", "saveTo:MAPElitesCountNegativeAccumulateChange",
					"minecraftContainsWholeMAPElitesArchive:true","forceLinearArchiveLayoutInMinecraft:false",
					"launchMinecraftServerFromJava:false",
					"io:true", "netio:true",
					//"io:false", "netio:false", 
					"mating:true", "fs:false",
					//"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.SimpleSolidBlockSet",
					"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet",
					//"minecraftTypeCountFitness:true",
					"minecraftChangeCenterOfMassFitness:true",
					"minecraftAccumulateChangeInCenterOfMass:false",
					"shortTimeBetweenMinecraftReads:500",
					"minecraftMandatoryWaitTime:1000",
					//"minecraftDiversityBlockFitness:true",
					//"minecraftTypeTargetFitness:true", 
					//"minecraftDesiredBlockCount:40",
					//"minecraftOccupiedCountFitness:true",
					//"minecraftEvolveOrientation:true",
					//"minecraftRedirectConfinedSnakes:true",
					//"minecraftStopConfinedSnakes:true", 
					"mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesWidthHeightDepthBinLabels",
					//"mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBlockCountEmptyCountBinLabels",
					"ea:edu.southwestern.evolution.mapelites.MAPElites", 
					"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
					"steadyStateIndividualsPerGeneration:100", 
					//FOR TESTING
					"spaceBetweenMinecraftShapes:10","parallelMAPElitesInitialize:true",
					//"minecraftXRange:9","minecraftYRange:9","minecraftZRange:9",
					"minecraftXRange:3","minecraftYRange:3","minecraftZRange:5",
					"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.ThreeDimensionalVolumeGenerator",
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
