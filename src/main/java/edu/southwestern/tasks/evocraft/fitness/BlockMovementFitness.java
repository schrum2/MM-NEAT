package edu.southwestern.tasks.evocraft.fitness;

import java.util.Arrays;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

public class BlockMovementFitness extends CheckBlocksInSpaceFitness {

	@Override
	public double fitnessFromBlocks(MinecraftCoordinates corner, List<Block> blocks) {
		long waitTime = Parameters.parameters.longParameter("minecraftMandatoryWaitTime");		
		int[][][] originalPosition = MinecraftClient.blockListTo3DArray(corner, blocks, 0);
		System.out.println("Original positions : " +blocks.toString());
		
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			System.out.print("Thread was interrupted");
			e.printStackTrace();
			System.exit(1);
		}
		
		List<Block> updatedBlocks = BlockMovementFitness.readBlocksFromClient(corner);
		
		int[][][] updatedPosition = MinecraftClient.blockListTo3DArray(corner, updatedBlocks, 0);
		System.out.println("Updated positions : " + Arrays.toString(updatedBlocks.stream().filter(b -> b.type() != BlockType.AIR.ordinal()).toArray()));
		
		double fitnessPoints = 0;
		// Compare the two lists now that it may have changed
		for(int i = 0; i < originalPosition.length; i++) {
			for(int j = 0; j < originalPosition[i].length; j++) {
				for(int k = 0; k < originalPosition[i][j].length; k++) {
					// If there is a change in blocks at a certain position, fitness goes up
					if(originalPosition[i][j][k] != updatedPosition[i][j][k]) {
						fitnessPoints++;
					}
				}
			}
		}
		return fitnessPoints;
	}

	@Override
	public double maxFitness() {
		return 0;
	}

}
