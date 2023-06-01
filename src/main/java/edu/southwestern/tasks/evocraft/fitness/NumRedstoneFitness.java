package edu.southwestern.tasks.evocraft.fitness;

import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
/**
 * extends TypeCountFitness with redstone
 * @author raffertyt
 *
 */
public class NumRedstoneFitness extends TypeCountFitness{

	public NumRedstoneFitness() {
		super(BlockType.REDSTONE_BLOCK.ordinal());
	}
	
}
