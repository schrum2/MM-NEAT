package edu.southwestern.tasks.evocraft.blocks;

import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;

/**
 * The original block set from the EvoCraft paper did not include observers.
 * @author Jacob Schrum
 *
 */
public class OriginalMachineBlockSet implements BlockSet {

	@Override
	public BlockType[] getPossibleBlocks() {
		return new BlockType[] {BlockType.QUARTZ_BLOCK, BlockType.SLIME, BlockType.REDSTONE_BLOCK, BlockType.PISTON, BlockType.STICKY_PISTON};
	}

}
