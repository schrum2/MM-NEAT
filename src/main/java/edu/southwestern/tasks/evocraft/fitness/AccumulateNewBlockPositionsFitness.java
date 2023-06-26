package edu.southwestern.tasks.evocraft.fitness;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.util.datastructures.Pair;

/**
 * Accumulates the number of blocks whose positions differ in consecutive snapshots
 * from reading the evaluation space for a shape.
 * 
 * @author schrum2
 *
 */
public class AccumulateNewBlockPositionsFitness extends TimedEvaluationMinecraftFitnessFunction {
	
	
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
		HashSet<MinecraftCoordinates> fitness = null;
		List<Block> originalShape = history.get(0).t2;
		for(int i = 0; i < history.size(); i++) {
			for(int j = 0; j < history.get(i).t2.size(); j++) {
				 fitness.add(history.get(i).t2.get(j).blockPosition());
			}
		}
		
		return fitness.size();
	}

	@Override
	public double maxFitness() {
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		int volume = ranges.x() * ranges.z() * ranges.y();
		return ((minNumberOfShapeReadings()+1) * volume);
	}
	
}
