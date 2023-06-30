package edu.southwestern.tasks.evocraft.fitness;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;

/**
 * A single fitness function that can be a weighted sum of several fitness functions
 * 
 * @author schrum2
 *
 */
public abstract class MinecraftWeightedSumFitnessFunction extends MinecraftFitnessFunction {

	private List<TimedEvaluationMinecraftFitnessFunction> timedFitnessFunctions;
	private List<MinecraftFitnessFunction> plainFitnessFunctions;
	private List<Double> timedWeights;
	private List<Double> plainWeights;

	/**
	 * Plan to use given list of fitness functions with the given list of weights.
	 * @param fitnessFunctions fitness functions for Minecraft
	 * @param weights weight to multiply each fitness score by
	 */
	public MinecraftWeightedSumFitnessFunction(List<MinecraftFitnessFunction> fitnessFunctions, List<Double> weights) {
		timedFitnessFunctions = new ArrayList<>();
		plainFitnessFunctions = new ArrayList<>();

		timedWeights = new ArrayList<>();
		plainWeights = new ArrayList<>();

		for(int i = 0; i < fitnessFunctions.size(); i++) {
			MinecraftFitnessFunction ff = fitnessFunctions.get(i);
			// register the function for tracking/logging, but it does not affect selection
			MMNEAT.registerFitnessFunction(ff.getClass().getSimpleName(), false);
			
			if(ff instanceof TimedEvaluationMinecraftFitnessFunction) {
				timedFitnessFunctions.add((TimedEvaluationMinecraftFitnessFunction) ff);
				timedWeights.add(weights.get(i));
			} else {
				plainFitnessFunctions.add((TimedEvaluationMinecraftFitnessFunction) ff);
				plainWeights.add(weights.get(i));
			}
		}		
	}
	
	@Override
	public double fitnessScore(MinecraftCoordinates shapeCorner, List<Block> originalBlocks) {
		//unsupported, should not be called, call weightedSumsFitnessScores instead
		throw new UnsupportedOperationException("weighted sum uses weightedSumsFitnessScores function instead of fitnessScore");
	}
		
	public Pair<Double, double[]> weightedSumsFitnessScores(MinecraftCoordinates shapeCorner, List<Block> originalBlocks){
		// Calculate timed and plain scores
		double[] timedScores = TimedEvaluationMinecraftFitnessFunction.multipleFitnessScores(timedFitnessFunctions, shapeCorner, originalBlocks);
		double[] plainScores = plainFitnessFunctions.parallelStream().mapToDouble(ff -> ff.fitnessScore(shapeCorner,originalBlocks)).toArray();
		
		// Multiply each score by its weight and add up the results
		double weightedSum = 0;
		for(int i = 0; i < timedScores.length; i++) {
			weightedSum += timedScores[i]*timedWeights.get(i);
		}
		for(int i = 0; i < plainScores.length; i++) {
			weightedSum += plainScores[i]*plainWeights.get(i);
		}
		
		Pair<Double, double[]> results = new Pair<>(weightedSum, ArrayUtil.combineArrays(timedScores, plainScores));
		return results;
	}
	
	@Override
	public double maxFitness() {

		double weightedSum = 0;
		for(int i = 0; i < timedWeights.size(); i++) {
			weightedSum += timedFitnessFunctions.get(i).maxFitness()*timedWeights.get(i);
		}
		for(int i = 0; i < plainWeights.size(); i++) {
			weightedSum += plainFitnessFunctions.get(i).maxFitness()*plainWeights.get(i);
		}
		
		return weightedSum;
	}

	/**
	 * Whether or not evaluating this function requires a Minecraft server
	 * to be instantiated. This is typically true if at least one of the
	 * fitness functions is a TimedEvaluationMinecraftFitnessFunction.
	 * 
	 * @return true of server needed for evaluation
	 */
	public abstract boolean needsSimulation();
}
