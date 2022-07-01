package edu.southwestern.tasks.evocraft.fitness;

import java.util.Arrays;
import java.util.List;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;

public abstract class PistonCountFitness extends CheckBlocksInSpaceFitness {

	protected Orientation[] allowedOrientations;
	
	public PistonCountFitness(Orientation[] allowedOrientations) {
		this.allowedOrientations = allowedOrientations;
	}
	
	@Override
	public double fitnessFromBlocks(MinecraftCoordinates corner, List<Block> blocks) {
		return pistonCount(blocks, allowedOrientations);
	}

	@Override
	public double maxFitness() {
		// TODO Auto-generated method stub
		return 0;
	}
	
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
