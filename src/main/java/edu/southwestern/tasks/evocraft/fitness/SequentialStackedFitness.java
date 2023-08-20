package edu.southwestern.tasks.evocraft.fitness;

import java.util.Arrays;
import java.util.List;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;

/**
 * This fitness function combined multiple fitness functions in order. 
 * A certain threshold needs to be passed with each fitness function
 * in order for later fitness functions to be able to contribute,
 * in which case the scores from different functions are added up.
 * However, later functions contribute nothing if the thresholds
 * for earlier functions are not surpassed.
 * 
 * @author Jacob Schrum
 *
 */
public abstract class SequentialStackedFitness extends MinecraftWeightedSumFitnessFunction {

	private List<Double> thresholds;

	public SequentialStackedFitness(List<MinecraftFitnessFunction> fitnessFunctions, List<Double> thresholds) {
		// Equally weight all objectives. The weights won't matter anyway.
		super(fitnessFunctions, Arrays.asList(ArrayUtil.objectDoubleSpecified(fitnessFunctions.size(), 1.0/fitnessFunctions.size())));
		this.thresholds = thresholds;
	}

	/**
	 * Simply adds up the unweighted component fitness scores, but only when the thresholds are exceeded.
	 * Earlier thresholds have to be surpassed in order for later scores to be added.
	 */
	public Pair<Double, double[]> weightedSumsFitnessScores(MinecraftCoordinates shapeCorner, List<Block> originalBlocks){
		// Get the weighted sum result, but ignore it, calculating a new fitness based on the raw scores
		Pair<Double, double[]> weightedSumResult = super.weightedSumsFitnessScores(shapeCorner, originalBlocks);
		
		double stackedFitness = 0;
		int thresholdIndex = 0;
		do { // Always include the first fitness
			stackedFitness += weightedSumResult.t2[thresholdIndex]; 
		} while(thresholdIndex < thresholds.size() && thresholds.get(thresholdIndex) < weightedSumResult.t2[thresholdIndex++]);
		// In the while above, notice that threshold increments after all checks, before next iteration
		
		return new Pair<>(stackedFitness, weightedSumResult.t2);
	}


}
