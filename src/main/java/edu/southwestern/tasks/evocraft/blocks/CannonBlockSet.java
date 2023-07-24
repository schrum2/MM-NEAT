package edu.southwestern.tasks.evocraft.blocks;

import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;

/**
 * Block set for creating moving machines with TNT block included.
 * @author Travis Rafferty
 *
 */
public class CannonBlockSet implements BlockSet {

	@Override
	public BlockType[] getPossibleBlocks() {
		return new BlockType[] {BlockType.REDSTONE_BLOCK, BlockType.OBSIDIAN, BlockType.TNT};
	}

}
