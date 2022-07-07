package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

/**
 * Counts number of pistons of either type (regular or sticky).
 * Extending TypeCountFitness only makes calculation of maxFitness easier.
 * Two internal instances of TypeCountFitness, one for each type.
 * 
 * @author Jacob Schrum
 *
 */
public class NumPistonsFitness extends TypeCountFitness {

	private TypeCountFitness piston;
	private TypeCountFitness stickyPiston;

	public NumPistonsFitness() {
		piston = new TypeCountFitness(BlockType.PISTON.ordinal());
		stickyPiston = new TypeCountFitness(BlockType.STICKY_PISTON.ordinal());
	}
	
	@Override
	public double fitnessScore(MinecraftCoordinates corner, List<Block> blocks) {
		//System.out.println(blocks);
		double total = piston.fitnessScore(corner,blocks);
		total += stickyPiston.fitnessScore(corner,blocks);
		return total;
	}
}
