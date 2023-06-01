package edu.southwestern.tasks.evocraft.fitness;

import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
/**
 * extends TypeCountFitness with air
 * @author raffertyt
 *
 */
public class NumAirFitness extends TypeCountFitness{

	public NumAirFitness() {
		super(BlockType.AIR.ordinal());
	}
	
}
