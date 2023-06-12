package edu.southwestern.tasks.evocraft.fitness;

import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
/**
 * Counts air blocks in a shape using the original blocks size and the volume of the max space a shape can take up. 
 * @author raffertyt
 *
 */
public class NumObsidianFitness extends TypeCountFitness{

		
	public NumObsidianFitness() {
		super(BlockType.OBSIDIAN.ordinal());
	}
	
}
