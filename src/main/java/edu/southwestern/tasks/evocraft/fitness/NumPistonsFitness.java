package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

public class NumPistonsFitness extends TypeCountFitness{

	@Override
	public double fitnessFromBlocks(MinecraftCoordinates corner, List<Block> blocks) {
		
		TypeCountFitness piston = new TypeCountFitness(BlockType.PISTON.ordinal());
		double total = piston.fitnessFromBlocks(corner,blocks);
		TypeCountFitness stickyPiston = new TypeCountFitness(BlockType.STICKY_PISTON.ordinal());
		total += stickyPiston.fitnessFromBlocks(corner,blocks);
		//System.out.println("Piston"+total);
		return total;
	}
}
