package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

public class NumRedstoneFitness extends TypeCountFitness{

	@Override
	public double fitnessFromBlocks(MinecraftCoordinates corner, List<Block> blocks) {
		
		TypeCountFitness piston = new TypeCountFitness(BlockType.REDSTONE_BLOCK.ordinal());
		double total = piston.fitnessFromBlocks(corner,blocks);
		return total;
		
//		int pistonCount = 0;
//		// increases count only if redstone block
//		for(Block b : blocks) {
//			if(b.type() == BlockType.PISTON.ordinal()||b.type() == BlockType.STICKY_PISTON.ordinal()) {
//				pistonCount++;
//			}
//		}
//		return pistonCount;
	}
}
