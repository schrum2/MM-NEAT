package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

/**
 * Abstract class for extending specific Minecraft fitness functions
 * 
 * @author schrum2
 */
public abstract class MinecraftFitnessFunction {

	/**
	 * Calculate fitness of one shape in the world based on its minimal corner coordinates.
	 * @param corner Minimal coordinates of shape
	 * @param originalBlocks Blocks from the generator, before being spawned (and potentially changing)
	 * @return Fitness score for shape
	 */
	public abstract double fitnessScore(MinecraftCoordinates corner, List<Block> originalBlocks);
	
	/**
	 * Minimum possible fitness
	 * @return Minimum possible fitness
	 */
	public double minFitness() {
		return 0; // Can be overridden
	}
	
	/**
	 * Maximum possible fitness
	 * @return Maximum possible fitness
	 */
	public abstract double maxFitness();
}
