package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

public class NumPistonsFitness extends CheckBlocksInSpaceFitness{

	@Override
	public double fitnessFromBlocks(MinecraftCoordinates corner, List<Block> blocks) {
		int pistonCount = 0;
		// increases count only if redstone block
		for(Block b : blocks) {
			if(b.type() == BlockType.PISTON.ordinal()||b.type() == BlockType.STICKY_PISTON.ordinal()) {
				pistonCount++;
			}
		}
		return pistonCount;
	}

	@Override
	public double maxFitness() {
		return Parameters.parameters.integerParameter("minecraftXRange") * Parameters.parameters.integerParameter("minecraftYRange") * Parameters.parameters.integerParameter("minecraftZRange");
	}

}