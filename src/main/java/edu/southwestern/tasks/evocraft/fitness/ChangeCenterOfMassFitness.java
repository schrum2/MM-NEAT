package edu.southwestern.tasks.evocraft.fitness;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

public class ChangeCenterOfMassFitness extends MinecraftFitnessFunction{


	@Override
	public double maxFitness() {
		return 0;
	}

	@Override
	public double fitnessScore(MinecraftCoordinates corner) {
		
		// Ranges before the change of space in between
		int xrange = Parameters.parameters.integerParameter("minecraftXRange");
		int yrange = Parameters.parameters.integerParameter("minecraftYRange");
		int zrange = Parameters.parameters.integerParameter("minecraftZRange");
		
		// Setting the space in between to be large and storing it
		Parameters.parameters.setInteger("spaceBetweenMinecraftShapes", 100);
		int inBetween = Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes");
		
		// Changing the size of the ranges with space in between
		Parameters.parameters.setInteger("minecraftXRange", xrange + inBetween);
		Parameters.parameters.setInteger("minecraftYRange", yrange + inBetween);
		
		// List of blocks in the area based on the corner
		List<Block> blocks = CheckBlocksInSpaceFitness.readBlocksFromClient(corner);
		
		// Initial center of mass is where it starts
		MinecraftCoordinates initialCenterOfMass = getCenterOfMass(blocks);
		
		// Wait for the machine to move some (if at all)
		long waitTime = Parameters.parameters.longParameter("minecraftMandatoryWaitTime");	
		
		// Final center of mass is where it ends up after the wait time
		MinecraftCoordinates finalCenterOfMass = getCenterOfMass(blocks);
		
		// Change in position could be in any of these directions (I believe)
		int changeInPosition = (int) (Math.sqrt(Math.pow(finalCenterOfMass.x()-initialCenterOfMass.x(),2)) + Math.pow(finalCenterOfMass.y()-initialCenterOfMass.y(),2) + Math.pow(finalCenterOfMass.z()-initialCenterOfMass.z(),2));
		return changeInPosition;
	}
	
	public static MinecraftCoordinates getCenterOfMass(List<Block> blocks) {
		int x = 0;
		int y = 0;
		int z = 0;
		
		for(int i = 0; i < blocks.size(); i++) {
			Block b = blocks.get(i);
			x += b.x();
			y += b.y();
			z += b.z();
		}
		
		int avgX = x/blocks.size();
		int avgY = y/blocks.size();
		int avgZ = z/blocks.size();
		
		MinecraftCoordinates centerOfMass = new MinecraftCoordinates(avgX,avgY,avgZ);
		
		return centerOfMass;
	}

	@Override
	public double minFitness() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
