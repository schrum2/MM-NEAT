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
		int total=0, maxX=0, maxY=0, maxZ=0, minX=0, minY=0, minZ=0;
		for(Block b : blocks) {
			
			// If any new min or max, change to it. (There is definitely a better way to do this)
			if(b.x()>maxX) maxX=b.x();
			if(b.x()<minX) minX=b.x();
			if(b.x()>maxY) maxX=b.y();
			if(b.x()<minY) minX=b.y();
			if(b.x()>maxZ) maxX=b.z();
			if(b.x()<minZ) minX=b.z();
			
			if(b.type() != BlockType.AIR.ordinal()) {
				total++;
			}
		}
		int sizeOfShape = (maxX-minX)*(maxY-minY)*(maxZ-minZ);
		int negativeBlocks = sizeOfShape-total;
		
		return negativeBlocks;
	}

	@Override
	public double maxFitness() {
		
		return Parameters.parameters.integerParameter("minecraftXRange") * Parameters.parameters.integerParameter("minecraftYRange") * Parameters.parameters.integerParameter("minecraftZRange")-2;
	}
	
}
