package edu.southwestern.tasks.evocraft.fitness;

import java.util.IntSummaryStatistics;
import java.util.List;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;

/**
 * Size along x-axis
 * 
 * @author schrum2
 *
 */
public class WidthFitness extends CheckBlocksInSpaceFitness {

	@Override
	public double fitnessFromBlocks(List<Block> blocks) {
		IntSummaryStatistics stats = blocks.parallelStream()
				// Remove AIR blocks
				.filter(b -> b.type() != BlockType.AIR.ordinal())
				// Get only the x-coordinates
				.mapToInt(b -> b.x())
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
