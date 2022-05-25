package edu.southwestern.tasks.evocraft.fitness;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.tasks.evocraft.MinecraftClient;

/**
 * Abstract class for extending specific Minecraft fitness functions
 * 
 * @author schrum2
 */
public abstract class MinecraftFitnessFunction {
	/**
	 * Each shape is generated with respect to a particular location that defines the
	 * minimal corner coordinates of the generated shape. Method uses these corner
	 * coordinates to find each shape and evaluate them, assigning fitness scores
	 * that are returned in a list.
	 * 
	 * @param corners List of corner locations
	 * @return List of corresponding fitness values
	 */
	public List<Double> fitnessScores(List<MinecraftClient.MineCraftCoordinates> corners){
		ArrayList<Double> fitnessScores = new ArrayList<>(corners.size());
		for(MinecraftClient.MineCraftCoordinates c : corners) {
			fitnessScores.add(fitnessScore(c));
		}
		return fitnessScores;
	}
	
	/**
	 * Calculate fitness of one shape in the world based on its minimal corner coordinates.
	 * @param corner Minimal coordinates of shape
	 * @return Fitness score for shape
	 */
	public abstract double fitnessScore(MinecraftClient.MineCraftCoordinates corner);
}
