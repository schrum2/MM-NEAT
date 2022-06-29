package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

public class FakeTestFitness extends CheckBlocksInSpaceFitness {
	public static double score=0;

	@Override
	public double fitnessFromBlocks(MinecraftCoordinates corner, List<Block> blocks) {
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.print("Thread was interrupted");
			e.printStackTrace();
			System.exit(1);
		}
		
		score++;
		return score;
	}

	@Override
	public double maxFitness() {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
