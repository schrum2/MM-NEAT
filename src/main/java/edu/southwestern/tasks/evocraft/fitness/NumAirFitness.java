package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
/**
 *Counts air blocks in a shape using the original blocks size and the volume of the max space a shape can take up. 
 * @author raffertyt
 *
 */
public class NumAirFitness extends MinecraftFitnessFunction{

		
	int max;
	public NumAirFitness() {
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		max = ranges.x() * ranges.z() * ranges.y();
	}
	
	@Override
	public double fitnessScore(MinecraftCoordinates corner, List<Block> originalBlocks) {
		//Max valume a shape can take up - blocks other than air will return air blocks
		return max - originalBlocks.size();
	}

	@Override
	public double minFitness() {
		return 0;
	}
	//Max valume a shape can take up
	@Override
	public double maxFitness() {
		return max;
	}
	
}
