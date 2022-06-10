package edu.southwestern.tasks.evocraft.fitness;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.function.ToIntFunction;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

/**
 * Size along a given axis
 * 
 * @author schrum2
 *
 */
public abstract class RangeFitness extends CheckBlocksInSpaceFitness {

	@Override
	public double fitnessFromBlocks(MinecraftCoordinates corner, List<Block> blocks) {
		if(blocks.size() == 0) return 0; // upcoming calculations do not work well on empty list
		return coordinateRange(blocks, dimension());
	}

	/**
	 * Method to access either x, y, or z coordinate of a Block
	 * @return method: Either Block::x, Block::y, or Block::z
	 */
	public abstract ToIntFunction<Block> dimension();

	/**
	 * Check all coordinates in a list of blocks to see what the range between the min and max
	 * is (inclusive)
	 * 
	 * @param blocks List of Minecraft blocks read from the world
	 * @param dimensionMethod Defines dimension to look at: Block::x, Block::y, or Block::z
	 * @return size along given dimension
	 */
	private double coordinateRange(List<Block> blocks, ToIntFunction<Block> dimensionMethod) {
		IntSummaryStatistics stats = blocks.parallelStream()
				// Remove AIR blocks
				.filter(b -> b.type() != BlockType.AIR.ordinal())
				// Get only the x-coordinates
				.mapToInt(dimensionMethod)
				.summaryStatistics();

		int min = stats.getMin();
		int max = stats.getMax();

		return max - min + 1;
	}

	@Override
	public double maxFitness() {
		return Parameters.parameters.integerParameter("minecraftXRange");
	}

}
