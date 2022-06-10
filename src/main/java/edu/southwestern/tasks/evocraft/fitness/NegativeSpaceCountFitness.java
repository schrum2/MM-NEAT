package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

public class NegativeSpaceCountFitness extends CheckBlocksInSpaceFitness {

	@Override
	public double fitnessFromBlocks(MinecraftCoordinates corner, List<Block> blocks) {
		// Initializes all variables to 0, need min and max coordinates to calculate the space the shape takes up
		int total=0, maxX=0, maxY=0, maxZ=0, 
				minX=1000, minY=1000, minZ=1000;
		for(Block b : blocks) {
			if(b.type() != BlockType.AIR.ordinal()) {
				// If any new min or max, change to it. Air blocks cannot be included in these coordinates (There is definitely a better way to do this)
				if(b.x()>maxX) maxX=b.x() % Parameters.parameters.integerParameter("minecraftXRange")+1;
				if(b.x()<minX) minX=b.x() % Parameters.parameters.integerParameter("minecraftXRange")+1;
				if(b.y()>maxY) maxY=b.y() % Parameters.parameters.integerParameter("minecraftYRange")+1;
				if(b.y()<minY) minY=b.y() % Parameters.parameters.integerParameter("minecraftYRange")+1;
				if(b.z()>maxZ) maxZ=b.z() % Parameters.parameters.integerParameter("minecraftZRange")+1;
				if(b.z()<minZ) minZ=b.z() % Parameters.parameters.integerParameter("minecraftZRange")+1;
			
				// Total keeps track of all of the blocks that are not air within the shape
				total++;
			}
		}
		// Computes size of the shape based on the coordinates, then subtracts the non-air blocks to get negative space
		System.out.println("X:"+(maxX-minX)+" Y:"+(maxY-minY)+" Z:"+(maxZ-minZ));
		int sizeOfShape = (maxX-minX)*(maxY-minY)*(maxZ-minZ);
		int negativeBlocks = sizeOfShape-total;
		System.out.println("Size of shape = "+sizeOfShape+" Total = "+total);
		System.out.println("Negative Blocks:"+negativeBlocks);
		return negativeBlocks;
	}

	@Override
	public double maxFitness() {
		// The max fitness here is the largest possible cube -2 because to get that designated negative space, there must be at least 2 cubes
		return Parameters.parameters.integerParameter("minecraftXRange") * Parameters.parameters.integerParameter("minecraftYRange") * Parameters.parameters.integerParameter("minecraftZRange")-2;
	}
	
}
