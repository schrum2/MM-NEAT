package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;

/**
 * Number of non-AIR blocks in the space
 * 
 * @author schrum2
 *
 */
public class OccupiedCountFitness extends CheckBlocksInSpaceFitness {

	@Override
	public double fitnessFromBlocks(List<Block> blocks) {
		int total = 0;
		for(Block b : blocks) {
			if(b.type() != BlockType.AIR.ordinal()) {
				total++;
			}
		}
		
		return total;
	}

	@Override
	public double maxFitness() {
		// TODO: Might not be appropriate when evolving snakes
		return Parameters.parameters.integerParameter("minecraftXRange") * Parameters.parameters.integerParameter("minecraftYRange") * Parameters.parameters.integerParameter("minecraftZRange");
	}

}
