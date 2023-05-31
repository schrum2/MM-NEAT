package edu.southwestern.tasks.evocraft.fitness;

import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;

public class NumWaterFitness extends TypeCountFitness{

	public NumWaterFitness() {
		super(BlockType.FLOWING_WATER.ordinal());
	}
	
}
