package edu.southwestern.tasks.evocraft.fitness;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.util.datastructures.Pair;

/**
 * fitness function that rewards shapes based on their total volume. Keeps track of max and min coordinates to calculate volume.
 * @author raffertyt
 *
 */
public class MaximizeVolumeFitness extends TimedEvaluationMinecraftFitnessFunction {

	
	//Keeps track of max and min coordinates to eventually get the volume
	@Override
	public double calculateFinalScore(ArrayList<Pair<Long, List<Block>>> history, MinecraftCoordinates corner,
			List<Block> originalBlocks) {

		MinecraftCoordinates maxResultCoordinates = new MinecraftCoordinates(0, 0, 0);
		MinecraftCoordinates minResultCoordinates = new MinecraftCoordinates(0, 0, 0);
		List<Block> currentShapeBlockList;
		for (int i = 0; i < history.size(); i++) {
			currentShapeBlockList = history.get(i).t2;
			minResultCoordinates = MinecraftUtilClass.minCoordinates(currentShapeBlockList);
			maxResultCoordinates = MinecraftUtilClass.maxCoordinates(currentShapeBlockList);
		}
	
		return MinecraftUtilClass.volume(minResultCoordinates, maxResultCoordinates);
	}
	//maximum number of blocks that could be of the desired type (if the whole evaluation area was filled)
	@Override
	public double maxFitness() {
		return MinecraftUtilClass.reservedSpace().x()*MinecraftUtilClass.reservedSpace().y();
	}

	
}
