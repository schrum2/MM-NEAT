package edu.southwestern.tasks.evocraft.fitness;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.util.datastructures.Pair;

/**
 * Fitness based on the number of distinct positions that a block of the shape
 * occupies over the history of evaluation.
 * 
 * @author Travis Rafferty
 *
 */
public class AccumulateNewBlockPositionsFitness extends TimedEvaluationMinecraftFitnessFunction {

	// Shape needs to visit this times as many blocks at in the shape positions in order to be saved
	private static final double SAVE_MULTIPLE = 2.5;
	
	@Override
	public Double earlyEvaluationTerminationResult(MinecraftCoordinates corner, List<Block> originalBlocks,
			ArrayList<Pair<Long, List<Block>>> history, List<Block> newShapeBlockList) {

		int numReadings = history.size();
		if(history.get(numReadings - 1).t2.equals(history.get(numReadings - 2).t2)) {
			// Shape is not changing, so just compute the final score already
			return this.calculateFinalScore(history, corner, originalBlocks);
		}

		return null; // Do not end early
	}

	@Override
	public double calculateFinalScore(ArrayList<Pair<Long, List<Block>>> history, MinecraftCoordinates corner,
			List<Block> originalBlocks) {
		// Set will not store duplicate points, only one copy of each point visited
		HashSet<MinecraftCoordinates> fitness = new HashSet<MinecraftCoordinates>();
		for(int i = 0; i < history.size(); i++) {
			List<Block> shape = history.get(i).t2;
			for(int j = 0; j < shape.size(); j++) {
				fitness.add(shape.get(j).blockPosition());
			}
		}

		return fitness.size();
	}
	@Override
	public boolean shapeIsWorthSaving(double fitnessScore, ArrayList<Pair<Long, List<Block>>> history, MinecraftCoordinates shapeCorner, List<Block> originalBlocks) {
		// A certain multiple of blocks beyond the initial count implies the whole shape has moved to inhabit more space
		return (fitnessScore > (originalBlocks.size() * SAVE_MULTIPLE));
	}
	@Override
	public double maxFitness() {
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		int volume = ranges.x() * ranges.z() * ranges.y();
		// Assumes all blocks move to new positions on every reading, which is unreasonably fast
		return ((minNumberOfShapeReadings()+1) * volume);
	}

}
