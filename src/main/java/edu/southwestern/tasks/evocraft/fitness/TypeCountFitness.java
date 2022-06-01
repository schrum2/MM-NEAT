package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

/**
 * Fitness score is the number of occurrences of a block of a specific type within the bounds of the generated shape.
 * @author schrum2
 *
 */
public class TypeCountFitness extends CheckBlocksInSpaceFitness {

	@Override
	public double fitnessFromBlocks(MinecraftCoordinates corner, List<Block> blocks) {
		return typeCount(blocks);
	}

	public static double typeCount(List<Block> blocks) {
		int desiredType = Parameters.parameters.integerParameter("minecraftDesiredBlockType");
		int total = 0;
		for(Block b : blocks) {
			if(b.type() == desiredType) {
				total++;
			}
		}
		
		return total;
	}

	@Override
	public double maxFitness() {
		return Parameters.parameters.integerParameter("minecraftXRange") * Parameters.parameters.integerParameter("minecraftYRange") * Parameters.parameters.integerParameter("minecraftZRange");
	}

}
