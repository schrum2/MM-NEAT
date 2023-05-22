package edu.southwestern.tasks.evocraft.blocks;

import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
/**
 * All possible blocks
 * @author raffertyt
 *
 */
public class AllBlocksBlockSet implements BlockSet {

	@Override
	public BlockType[] getPossibleBlocks() {
		return MinecraftClient.BlockType.values();
	}

}
