package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;

/**
 * evaluates the pistons of allowed orientations and calculate bins
 * maxFitness indicates the bin for this shape
 * fitnessScore returns the number of pistons of the allowed orientation being evaluated
 * 
 * @author comments lewisj
 *
 */
public abstract class PistonCountFitness extends MinecraftFitnessFunction {

	private Orientation[] allowedOrientations;
	
	/**
	 * it constructs an instance that only counts pistons of the passed orientations
	 * @param allowedOrientations the orientations being evaluated for this instance
	 */
	public PistonCountFitness(Orientation[] allowedOrientations) {
		this.allowedOrientations = allowedOrientations;
	}
	
	@Override
	public double fitnessScore(MinecraftCoordinates corner, List<Block> blocks) {
		return pistonCount(blocks, allowedOrientations);
	}

	@Override
	public double maxFitness() {
		MinecraftCoordinates range = MinecraftUtilClass.getRanges();
		return range.x() * range.y() * range.z();
	}
	
	/**
	 * counts the number of pistons in two directions given by the allowed orientations
	 * 
	 * @param blocks the list of blocks for the given shape
	 * @param allowedOrientations the orientations being evaluated
	 * @return the total number of pistons with the allowed orientations
	 */
	public static double pistonCount(List<Block> blocks, Orientation[] allowedOrientations) {
		int total = 0;
		List<Block> pistons = MinecraftUtilClass.getDesiredBlocks(blocks, new BlockType[] {BlockType.PISTON, BlockType.STICKY_PISTON});
		for(Block p : pistons) {
			if(p.orientation() == allowedOrientations[0].ordinal() || p.orientation() == allowedOrientations[1].ordinal()) {
				total++;
			}
		}
		return total;
	}

	
}
