package edu.southwestern.tasks.evocraft.fitness;

import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;

public class WaterLavaSecondaryCreationFitness extends SecondaryBlockCreationFitness{
	
	public WaterLavaSecondaryCreationFitness() {
		super(new BlockType[] {BlockType.COBBLESTONE, BlockType.OBSIDIAN, BlockType.STONE});
	}
}