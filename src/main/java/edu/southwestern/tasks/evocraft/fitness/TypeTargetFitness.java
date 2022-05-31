package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;

public class TypeTargetFitness extends CheckBlocksInSpaceFitness {

	@Override
	public double fitnessFromBlocks(List<Block> blocks) {
		int desiredType = Parameters.parameters.integerParameter("minecraftDesiredBlockType");
		int desiredTotal = Parameters.parameters.integerParameter("minecraftDesiredBlockCount");
		//double blockCount = use type count to get total?
		int blockCount = 0;
		for(Block b : blocks) {
			if(b.type() == desiredType) {
				blockCount++;
			}
		}
		
		return desiredTotal - Math.abs(desiredTotal - blockCount);
	}

	@Override
	public double maxFitness() {
		// Max fitness for TypeTarget is the desired block count
		return Parameters.parameters.integerParameter("minecraftDesiredBlockCount");
	}

}
