package edu.southwestern.tasks.evocraft.characterizations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.evolution.mapelites.BaseBinLabels;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;

public abstract class MinecraftMAPElitesBinLabels extends BaseBinLabels {

	public MinecraftMAPElitesBinLabels() {
		// if populating the world with the archive, clear it all here
		// call List<String> binLabels() and dimensionSizes()
		
		// Putting clearing here because bin labels and other aspects need to be initialized first in the constructor before clearing can occur
		List<String> binSize = binLabels();
		// Gets ranges for all coordinates
		MinecraftCoordinates ranges = new MinecraftCoordinates(Parameters.parameters.integerParameter("minecraftXRange"),Parameters.parameters.integerParameter("minecraftYRange"),Parameters.parameters.integerParameter("minecraftZRange"));
		// Clears the area
		MinecraftClient.getMinecraftClient().clearSpaceForShapes(new MinecraftCoordinates(0,MinecraftClient.GROUND_LEVEL+1,0), ranges, binSize.size(), Math.max(Parameters.parameters.integerParameter("minecraftMaxSnakeLength"), MinecraftClient.BUFFER));
	}
	
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
		return properties().parallelStream().map(ff -> ff.getClass().getSimpleName()).toArray(String[]::new);
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
