package edu.southwestern.tasks.evocraft.fitness;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import edu.southwestern.parameters.Parameters;
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
		
		// Setting the space in between to be large and storing it
		// Might need to be bigger!
		int inBetween = Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes");
		
		// Shifts over the corner to the new range with the large space in between shapes
		int shift = Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes") / 2;
		corner = new MinecraftCoordinates(corner.x() - shift, corner.y(), corner.z()- shift);
		MinecraftCoordinates end = corner.add(new MinecraftCoordinates(xrange + inBetween, 0, zrange + inBetween));
		
		// List of blocks in the area based on the corner
		List<Block> blocks = CheckBlocksInSpaceFitness.readBlocksFromClient(corner,end);
		
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
		blocks = CheckBlocksInSpaceFitness.readBlocksFromClient(corner,end);
		
		//System.out.println("List of blocks after movement: "+ Arrays.toString(blocks.stream().filter(b -> b.type() != BlockType.AIR.ordinal()).toArray()));
		
		// Final center of mass is where it ends up after the wait time
		Vertex finalCenterOfMass = getCenterOfMass(blocks);
		
		//System.out.println(finalCenterOfMass);
		
		// Change in position could be in any of these directions (I believe)
		//double changeInPosition = (Math.sqrt(Math.pow(finalCenterOfMass.x()-initialCenterOfMass.x(),2)) + Math.pow(finalCenterOfMass.y()-initialCenterOfMass.y(),2) + Math.pow(finalCenterOfMass.z()-initialCenterOfMass.z(),2));
		double changeInPosition = finalCenterOfMass.distance(initialCenterOfMass);
		return changeInPosition;
	}
	
	public static Vertex getCenterOfMass(List<Block> blocks) {
		double x = 0;
		double y = 0;
		double z = 0;
		
		List<Block> filteredBlocks = blocks.stream().
				filter( b -> b.type() != BlockType.AIR.ordinal() && b.type() != BlockType.DIRT.ordinal() && b.type() != BlockType.GRASS.ordinal()).
				collect(Collectors.toList());
		
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
	
}
