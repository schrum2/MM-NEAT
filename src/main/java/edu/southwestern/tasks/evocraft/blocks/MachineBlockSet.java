package edu.southwestern.tasks.evocraft.blocks;

import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;

/**
 * Block set for creating moving machines.
 * @author Jacob Schrum
 *
 */
public class MachineBlockSet implements BlockSet {

	@Override
	public BlockType[] getPossibleBlocks() {
		return new BlockType[] {BlockType.QUARTZ_BLOCK, BlockType.SLIME, BlockType.REDSTONE_BLOCK, BlockType.PISTON, BlockType.STICKY_PISTON, BlockType.OBSERVER};
	}

}
