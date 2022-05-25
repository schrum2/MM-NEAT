package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.*;

public class TypeCountFitness extends MinecraftFitnessFunction {

	@Override
	public double fitnessScore(MineCraftCoordinates corner) {
		MinecraftClient client = MinecraftClient.getMinecraftClient();
		MineCraftCoordinates ranges = new MineCraftCoordinates(
				Parameters.parameters.integerParameter("minecraftXRange") - 1,
				Parameters.parameters.integerParameter("minecraftYRange") - 1,
				Parameters.parameters.integerParameter("minecraftZRange") - 1);
		List<Block> blocks = client.readCube(corner, corner.add(ranges));
		int desiredType = Parameters.parameters.integerParameter("minecraftDesiredBlockType");
		int total = 0;
		for(Block b : blocks) {
			if(b.type() == desiredType) {
				total++;
			}
		}
		
		return total;
	}

}
