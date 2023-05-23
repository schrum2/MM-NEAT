package edu.southwestern.tasks.evocraft.blocks;

import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;

/**
 * Block set for creating moving machines using a set with flowing water and flowing lava
 * @author Joanna Blatt Lewis
 *
 */
public class WaterAndLavaBlockSet implements BlockSet {

	@Override
	public BlockType[] getPossibleBlocks() {
		return new BlockType[] {BlockType.FLOWING_WATER, BlockType.FLOWING_LAVA, BlockType.QUARTZ_BLOCK, BlockType.SLIME, BlockType.REDSTONE_BLOCK, BlockType.PISTON, BlockType.STICKY_PISTON, BlockType.OBSERVER};
	}

}
