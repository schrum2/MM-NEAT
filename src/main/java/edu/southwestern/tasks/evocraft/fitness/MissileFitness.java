package edu.southwestern.tasks.evocraft.fitness;

import java.util.ArrayList;
import java.util.List;

import com.clearspring.analytics.util.Pair;

import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;


/**
 * 
 * @author raffertyt
 *
 */
public class MissileFitness extends TimedEvaluationMinecraftFitnessFunction {

	private MinecraftCoordinates targetCornerOffset;
	
	public MissileFitness() {
		//targetCornerOffset
	}

	//shape dimensions
	@Override
	public double maxFitness() {
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		double max = ranges.x() * ranges.z() * ranges.y();
		return -max;
	}

	@Override
	public double fitnessScore(MinecraftCoordinates corner, List<Block> originalBlocks) {
		
		// TODO: Create target structure here
		
		return super.fitnessScore(corner, originalBlocks);
	}
	
	
	@Override
	public double calculateFinalScore(ArrayList<Pair<Long, List<Block>>> history, MinecraftCoordinates corner,
			List<Block> originalBlocks) {
		// TODO Auto-generated method stub
		return 0;
	}

	
}
