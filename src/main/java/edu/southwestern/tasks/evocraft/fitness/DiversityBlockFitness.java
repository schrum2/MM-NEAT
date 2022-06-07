package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.shapegeneration.ShapeGenerator;

public class DiversityBlockFitness extends CheckBlocksInSpaceFitness{

	@Override
	public double fitnessFromBlocks(MinecraftCoordinates corner, List<Block> blocks) {
		
		int[][][] shape = MinecraftClient.blockListTo3DArray(corner, blocks, 1);
		
		// For all blocks in the list of blocks
		int counter = 0;
		for(Block b : blocks) {
			// If the block is not air
			if(b.type() != BlockType.AIR.ordinal()) {
				// Look at all 6 of its neighbors
				for(int d = 0; d < ShapeGenerator.NUM_DIRECTIONS; d++) {
					// Array of length 3
					int[] direction = ShapeGenerator.nextDirection(d);
					if(shape[b.x() - corner.x() + 1 +direction[0]][b.y()- corner.y() + 1 +direction[1]][b.z()- corner.z() + 1 +direction[2]] != b.type()) counter++;
				}
			}
		}
		return counter;
	}

	@Override
	public double maxFitness() {
		return Parameters.parameters.integerParameter("minecraftXRange") * Parameters.parameters.integerParameter("minecraftYRange") * Parameters.parameters.integerParameter("minecraftZRange");
	}

}