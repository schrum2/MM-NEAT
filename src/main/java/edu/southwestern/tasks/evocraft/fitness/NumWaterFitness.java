package edu.southwestern.tasks.evocraft.fitness;

import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
/**
 * extends TypeCountFitness with water
 * @author raffertyt
 *
 */
public class NumWaterFitness extends TypeCountFitness{

	public NumWaterFitness() {
		super(BlockType.FLOWING_WATER.ordinal());
	}
	
}
