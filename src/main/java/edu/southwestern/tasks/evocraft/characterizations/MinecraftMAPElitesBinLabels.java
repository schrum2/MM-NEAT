package edu.southwestern.tasks.evocraft.characterizations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.evolution.mapelites.BaseBinLabels;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;

public abstract class MinecraftMAPElitesBinLabels extends BaseBinLabels {

	/**
	 * Collection of fitness functions that calculate scores to based the
	 * behavior characterization on.
	 * @return List of fitness functions for Minecraft
	 */
	public abstract List<MinecraftFitnessFunction> properties();
	
	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		// Assumes the keys for objects in the map are derived from the fitness function names, which dimensions() also uses.
		// Also assumes every fitness function returns a double which is stored as a Double, and has to be cast to double before being cast to int.
		return Arrays.stream(dimensions()).parallel().mapToInt(name -> (int) ((double) keys.get(name))).toArray();
	}

	@Override
	public String[] dimensions() {
		// Names of the fitness functions are the dimensions
		return properties().parallelStream().map(ff -> ff.getClass().getName()).toArray(String[]::new);
	}
	
	/**
	 * It is easy to generate an array of fitness scores from the list of fitness functions, which are the
	 * properties(). This method takes such an array and packages them in a HashMap since this representation
	 * is required elsewhere.
	 * 
	 * @param fitnessScores calculated fitness scores for each function in the properties/dimensions, and in the same order
	 * @return HashMap containing these scores with the names of the fitness functions as keys
	 */
	public HashMap<String,Object> behaviorMapFromScores(double[] fitnessScores) {
		HashMap<String,Object> behaviorMap = new HashMap<String,Object>();
		String[] functionNames = dimensions();
		for(int i = 0; i < functionNames.length; i++) {
			behaviorMap.put(functionNames[i], fitnessScores[i]);
		}
		return behaviorMap;
	}
}
