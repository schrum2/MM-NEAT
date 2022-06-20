package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

public class NumRedstoneFitness extends CheckBlocksInSpaceFitness{

	@Override
	public double fitnessFromBlocks(MinecraftCoordinates corner, List<Block> blocks) {
		int redstoneCount = 0;
		// increases count only if redstone block
		for(Block b : blocks) {
			if(b.type() == BlockType.REDSTONE_BLOCK.ordinal()) {
				redstoneCount++;
			}
		}
		return redstoneCount;
	}

	@Override
	public double maxFitness() {
		// TODO Auto-generated method stub
		return Parameters.parameters.integerParameter("minecraftXRange") * Parameters.parameters.integerParameter("minecraftYRange") * Parameters.parameters.integerParameter("minecraftZRange");
	}
	

}
