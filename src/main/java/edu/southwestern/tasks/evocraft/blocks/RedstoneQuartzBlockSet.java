package edu.southwestern.tasks.evocraft.blocks;

import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;

/**
 * Red stone and quartz
 * @author Jacob Schrum
 *
 */
public class RedstoneQuartzBlockSet implements BlockSet {

	@Override
	public BlockType[] getPossibleBlocks() {
		return new BlockType[] {BlockType.QUARTZ_BLOCK, BlockType.REDSTONE_BLOCK};
	}

}
