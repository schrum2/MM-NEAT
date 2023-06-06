package edu.southwestern.tasks.evocraft.fitness;

import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
/**
 * extends TypeCountFitness with Quartz
 * @author Jacob Schrum
 *
 */
public class NumQuartzFitness extends TypeCountFitness{

	public NumQuartzFitness() {
		super(BlockType.QUARTZ_BLOCK.ordinal());
	}
	
}
