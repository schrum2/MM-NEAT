package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

public class NumRedstoneFitness extends TypeCountFitness{

	@Override
	public double fitnessFromBlocks(MinecraftCoordinates corner, List<Block> blocks) {
		
		TypeCountFitness piston = new TypeCountFitness(BlockType.REDSTONE_BLOCK.ordinal());
		double total = piston.fitnessFromBlocks(corner,blocks);
		return total;
	}
}
