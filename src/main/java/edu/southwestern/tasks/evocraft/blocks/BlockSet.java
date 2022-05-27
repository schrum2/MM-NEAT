package edu.southwestern.tasks.evocraft.blocks;

import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;

/**
 * A list of allowable block types
 * @author schrum2
 *
 */
public interface BlockSet {
	/**
	 * Array of the types of blocks that are allowed in a given situation (e.g., for shape generation)
	 * @return Array of BlockTypes
	 */
	public BlockType[] getPossibleBlocks();
}
