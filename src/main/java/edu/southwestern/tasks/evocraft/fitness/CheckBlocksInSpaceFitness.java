package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

/**
 * Any fitness that is only based on the blocks in the space originally reserved 
 * for the generated shape.
 * 
 * @author schrum2
 *
 */
public abstract class CheckBlocksInSpaceFitness extends MinecraftFitnessFunction {

	/**
	 * Read all Blocks in the space reserved for the shape and calculate
	 * fitness by processing that list.
	 */
	@Override
	public double fitnessScore(MinecraftCoordinates corner) {
		MinecraftClient client = MinecraftClient.getMinecraftClient();
		MinecraftCoordinates ranges = new MinecraftCoordinates(
				Parameters.parameters.integerParameter("minecraftXRange") - 1,
				Parameters.parameters.integerParameter("minecraftYRange") - 1,
				Parameters.parameters.integerParameter("minecraftZRange") - 1);
		List<Block> blocks = client.readCube(corner, corner.add(ranges));
		return fitnessFromBlocks(blocks);
	}

	/**
	 * Details of how the list is processed determine the actual fitness value.
	 * @param blocks Blocks occupying the space reserved for the shape.
	 * @return A fitness score
	 */
	public abstract double fitnessFromBlocks(List<Block> blocks);

	@Override
	public double minFitness() {
		return 0;
	}

}