package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

/** 
 * Only used for testing. Keeps returning a fitness that is one higher than the previous.
 * 
 * @author Jacob Schrum
 *
 */
public class FakeTestFitness extends MinecraftFitnessFunction {
	public static double score=0;

	@Override
	public double fitnessScore(MinecraftCoordinates corner, List<Block> blocks) {
		
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
		return 0; // Incorrect, but not relevant
	}
	

}
