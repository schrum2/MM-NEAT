package edu.southwestern.tasks.evocraft.fitness;

import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;

public class NumRedstoneFitness extends TypeCountFitness{

	public NumRedstoneFitness() {
		super(BlockType.REDSTONE_BLOCK.ordinal());
	}
	
}
