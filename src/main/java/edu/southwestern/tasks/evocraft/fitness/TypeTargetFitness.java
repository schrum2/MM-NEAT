package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

public class TypeTargetFitness extends MinecraftFitnessFunction {

	@Override
	public double fitnessScore(MinecraftCoordinates corner, List<Block> blocks) {
		// total that is being evolved to match
		int desiredTotal = Parameters.parameters.integerParameter("minecraftDesiredBlockCount");
		// count of current total number of desired blocks
		double blockCount = TypeCountFitness.typeCount(blocks,Parameters.parameters.integerParameter("minecraftDesiredBlockType"));
		
		return desiredTotal - Math.abs(desiredTotal - blockCount);
	}

	@Override
	public double maxFitness() {
		// Max fitness for TypeTarget is the desired block count
		return Parameters.parameters.integerParameter("minecraftDesiredBlockCount");
	}

}
