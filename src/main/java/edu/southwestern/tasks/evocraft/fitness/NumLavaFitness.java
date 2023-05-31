package edu.southwestern.tasks.evocraft.fitness;

import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
/**
 * extends TypeCountFitness with redstone
 * @author raffertyt
 *
 */
public class NumLavaFitness extends TypeCountFitness{

	public NumLavaFitness() {
		super(BlockType.FLOWING_LAVA.ordinal());
	}
	
}
