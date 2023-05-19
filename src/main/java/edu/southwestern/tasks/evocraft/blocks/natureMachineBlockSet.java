package edu.southwestern.tasks.evocraft.blocks;

import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
/**
 * Machine block set with added leaves and logs
 * @author raffertyt
 *
 */

public class natureMachineBlockSet implements BlockSet {

	@Override
	public BlockType[] getPossibleBlocks() {
		return new BlockType[] {BlockType.QUARTZ_BLOCK, BlockType.SLIME, BlockType.REDSTONE_BLOCK, BlockType.PISTON, BlockType.STICKY_PISTON, BlockType.OBSERVER, BlockType.LEAVES, BlockType.LOG2};
	}

}