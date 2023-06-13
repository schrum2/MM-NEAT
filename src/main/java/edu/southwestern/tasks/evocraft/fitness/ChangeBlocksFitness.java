package edu.southwestern.tasks.evocraft.fitness;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.parameters.Parameters;
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
public class ChangeBlocksFitness extends TimedEvaluationMinecraftFitnessFunction {
	
	
	
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

		double fitness = 0;
		// Start at index 2 to skip the first shape (the only one with orientation information)
		List<Block> originalShape = history.get(0).t2;
		for(int i = 2; i < history.size(); i++) {
			// Remove the blocks that changed between consecutive readings
			List<Block> shape1 = history.get(i-1).t2;
			//System.out.println("shape 1:" + shape1);
			List<Block> shape2 = history.get(i).t2;
			//System.out.println("shape 2:" + shape2);
			List<Block> shape1minusShape2 = MinecraftUtilClass.shapeListDifference(shape1, shape2);
			//System.out.println("shape1minusShape2:" + shape1minusShape2);
			//System.out.println("change in center of mass:" + MinecraftUtilClass.getCenterOfMass(shape1).distance(MinecraftUtilClass.getCenterOfMass(shape2)));
			// If more blocks move, then less will stay the same, and fitness will be higher
			int difference = shape1minusShape2.size();
			//System.out.println("difference:" + difference);
			if(Parameters.parameters.booleanParameter("minecraftChangeBlocksMomentum")) {
				fitness += difference + MinecraftUtilClass.getCenterOfMass(originalShape).distance(MinecraftUtilClass.getCenterOfMass(shape2));
			} else {
				fitness += difference;
			}
			//System.out.println("fitness so far:" + fitness);
		}
		
		return fitness;
	}

	@Override
	public double maxFitness() {
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		int volume = ranges.x() * ranges.z() * ranges.y();
		// Assume the whole shape changes on each reading, which seems very unlikely
		return volume * (minNumberOfShapeReadings()+1);
	}

}
