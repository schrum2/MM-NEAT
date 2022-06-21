package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

public class NumRedstoneFitness extends TypeCountFitness{

	@Override
	public double fitnessFromBlocks(MinecraftCoordinates corner, List<Block> blocks) {
		TypeCountFitness redstone = new TypeCountFitness(BlockType.REDSTONE_BLOCK.ordinal());
		double total = redstone.fitnessFromBlocks(corner,blocks);
		return total;
		
//		int redstoneCount = 0;
//		// increases count only if redstone block
//		for(Block b : blocks) {
//			if(b.type() == BlockType.REDSTONE_BLOCK.ordinal()) {
//				redstoneCount++;
//			}
//		}
//		return redstoneCount;
	}
}
