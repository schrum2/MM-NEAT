package edu.southwestern.tasks.evocraft.blocks;

import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;

/**
 * Just a few simple solid blocks
 * @author Jacob Schrum
 *
 */
public class SimpleSolidBlockSet implements BlockSet {

	@Override
	public BlockType[] getPossibleBlocks() {
		return new BlockType[] {BlockType.QUARTZ_BLOCK, BlockType.REDSTONE_BLOCK, BlockType.COBBLESTONE};
	}

}
