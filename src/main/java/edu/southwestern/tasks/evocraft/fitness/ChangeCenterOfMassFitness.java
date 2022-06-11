package edu.southwestern.tasks.evocraft.fitness;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.util.datastructures.Vertex;

public class ChangeCenterOfMassFitness extends MinecraftFitnessFunction{


	@Override
	public double maxFitness() {
		// Probably overshoots a bit
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
		
		// Setting the space in between to be large and storing it
		// Might need to be bigger!
		int inBetween = Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes");
		
		// Shifts over the corner to the new range with the large space in between shapes
		int shift = Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes") / 2;
		corner = new MinecraftCoordinates(corner.x() - shift, corner.y(), corner.z() - shift);
		MinecraftCoordinates end = corner.add(new MinecraftCoordinates(xrange + inBetween, 0, zrange + inBetween));
		
		assert corner.x() <= end.x() && corner.y() <= end.y() && corner.z() <= end.z(): "corner should be less than end in each coordinate: corner = "+corner+ ", max = "+end; 
		
//		System.out.println("corner:"+corner);
//		System.out.println("end:"+end);
		
		// List of blocks in the area based on the corner
		List<Block> blocks = MinecraftClient.getMinecraftClient().readCube(corner,end);
		
		//System.out.println("List of blocks before movement: "+ Arrays.toString(blocks.stream().filter(b -> b.type() != BlockType.AIR.ordinal()).toArray()));
		
		// Initial center of mass is where it starts
		Vertex initialCenterOfMass = getCenterOfMass(blocks);
		
		//System.out.println(initialCenterOfMass);
		
		// Wait for the machine to move some (if at all)
		long waitTime = Parameters.parameters.longParameter("minecraftMandatoryWaitTime");
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			System.out.print("Thread was interrupted");
			e.printStackTrace();
			System.exit(1);
		}
		
		// Read in again to update the list
		List<Block> afterBlocks = filterOutAirDirtGrass(MinecraftClient.getMinecraftClient().readCube(corner,end));
		//System.out.println(afterBlocks);
		if (afterBlocks.isEmpty()) return maxFitness();
		else {

			//System.out.println("List of blocks after movement: "+ Arrays.toString(blocks.stream().filter(b -> b.type() != BlockType.AIR.ordinal()).toArray()));

			// Final center of mass is where it ends up after the wait time
			Vertex finalCenterOfMass = getCenterOfMass(afterBlocks);

			//System.out.println(finalCenterOfMass);

			// Change in position could be in any of these directions (I believe)
			//double changeInPosition = (Math.sqrt(Math.pow(finalCenterOfMass.x()-initialCenterOfMass.x(),2)) + Math.pow(finalCenterOfMass.y()-initialCenterOfMass.y(),2) + Math.pow(finalCenterOfMass.z()-initialCenterOfMass.z(),2));

			double changeInPosition = finalCenterOfMass.distance(initialCenterOfMass);
			assert !Double.isNaN(changeInPosition) : "Before: " + filterOutAirDirtGrass(blocks) + ", After:" + filterOutAirDirtGrass(afterBlocks);

			return changeInPosition;
		}
	}
	
	public static Vertex getCenterOfMass(List<Block> blocks) {
		double x = 0;
		double y = 0;
		double z = 0;
		
		List<Block> filteredBlocks = filterOutAirDirtGrass(blocks);
		
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

	private static List<Block> filterOutAirDirtGrass(List<Block> blocks) {
		return blocks.stream().
				filter( b -> b.type() != BlockType.AIR.ordinal() && b.type() != BlockType.DIRT.ordinal() && b.type() != BlockType.GRASS.ordinal()).
				collect(Collectors.toList());
	}

	@Override
	public double minFitness() {
		return 0;
	}
	
	public static void main(String[] args) {
		int seed = 1;
		try {
			MMNEAT.main(new String[] { "runNumber:" + seed, "randomSeed:" + seed, "trials:1", "mu:100", "maxGens:100000",
					"base:minecraft", "log:Minecraft-MAPElitesWHDFlyingMachineBig", "saveTo:MAPElitesWHDFlyingMachineBig",
					"minecraftContainsWholeMAPElitesArchive:true","forceLinearArchiveLayoutInMinecraft:false",
					"launchMinecraftServerFromJava:false",
					"io:true", "netio:true",
					//"io:false", "netio:false", 
					"mating:true", "fs:false",
					//"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.SimpleSolidBlockSet",
					"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet",
					//"minecraftTypeCountFitness:true",
					"minecraftChangeCenterOfMassFitness:true",
					"minecraftMandatoryWaitTime:1000",
					//"minecraftDiversityBlockFitness:true",
					//"minecraftTypeTargetFitness:true", 
					//"minecraftDesiredBlockCount:40",
					//"minecraftOccupiedCountFitness:true",
					//"minecraftEvolveOrientation:true",
					"minecraftRedirectConfinedSnakes:true",
					//"minecraftStopConfinedSnakes:true", 
					"mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesWidthHeightDepthBinLabels",
					"ea:edu.southwestern.evolution.mapelites.MAPElites", 
					"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
					"steadyStateIndividualsPerGeneration:100", 
					//FOR TESTING
					"spaceBetweenMinecraftShapes:10","parallelMAPElitesInitialize:true",
					//"minecraftXRange:9","minecraftYRange:9","minecraftZRange:9",
					"minecraftXRange:6","minecraftYRange:4","minecraftZRange:6",
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
