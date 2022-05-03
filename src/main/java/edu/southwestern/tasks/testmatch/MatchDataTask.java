package edu.southwestern.tasks.testmatch;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.tasks.motests.OptimizationDisplay;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.stats.StatisticsUtilities;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class takes input and output pairs that a network needs to evolve to
 * match returns a fitness value based on the Mean Squared Error (how far the
 * actual outputs are from the desired outputs)
 * 
 * @author Jacob Schrum
 * @param <T>
 *            Phenotype being evolved, which must be a network
 */
public abstract class MatchDataTask<T extends Network> extends LonerTask<T> implements NetworkTask {

	public static boolean pauseForEachCase = true;

	/**
	 * create a matchDataTask object with default values for the fitness
	 */
	public MatchDataTask() {
		MMNEAT.registerFitnessFunction("Error", null, true);
	}

	@Override
	/**
	 * a method that evaluated a given individual based on its genotype by
	 * providing a score (or fitness level)
	 * 
	 * @param individual
	 *            a genotype of a given agent
	 * @return the score of the individual
	 */
	public Score<T> evaluate(Genotype<T> individual) {
		// RandomNumbers.randomGenerator = new Random(0);
		ArrayList<Pair<double[], double[]>> trainingSet = getTrainingPairs();
		pauseForEachCase = trainingSet.size() <= 10; // Pausing for more than this would be tedious
		double averageError = calculateMatchLoss(individual, trainingSet);
		// Fitness is 1 - the loss, which should be positive and bounded by 1.
		return new Score<T>(individual, new double[] { 1 - averageError }, null); 
	}

	/**
	 * Calculating the fitness of the produced image to the target image by 
	 * comparing the desired outputs with the actual outputs.
	 * 
	 * @param individual The genotype of the image
	 * @param trainingSet A 2D ArrayList of pairs (type double) holding 
	 * 					  the coordinates of each pixel and the corresponding 
	 * 					  color
	 * @return Returns an array with the average squared energy error of the samples
	 */
	public static <T extends Network> double calculateMatchLoss(Genotype<T> individual, ArrayList<Pair<double[], double[]>> trainingSet) {
		// Creating an ArrayList out of pairs of doubles
		ArrayList<ArrayList<Pair<Double, Double>>> samples = new ArrayList<ArrayList<Pair<Double, Double>>>(trainingSet.size());
		Network n = individual.getPhenotype();	// Get the phenotype of individual and store it in a network
		
		OptimizationDisplay display = null;
		if(CommonConstants.watch) {
			display = new OptimizationDisplay();
		}
		
		// loop that runs for each "pattern" in the trainingSet, which is a pair
		// of double arrays of inputs/outputs
		for (Pair<double[], double[]> pattern : trainingSet) {
			double[] inputs = pattern.t1;	// an array of doubles of the inputs
			double[] desiredOutputs = pattern.t2;	// an array of doubles of the desired outputs
			n.flush(); // If modeling a regression or classification function, recurrency should not be possible 
			// find the actual outputs based on the given inputs
			double[] actualOutputs = n.process(inputs);
			if (CommonConstants.watch) {
				System.out.println("Desired: " + Arrays.toString(desiredOutputs) + ", Actual: " + Arrays.toString(actualOutputs));
				if(display != null) { // Will only show differences in first output ... not all if there are multiple
					// The final parameter is addToFront, meaning Pareto front. That is because
					// I am somewhat abusing the class OptimizationDisplay, which isn't meant for general plotting
					display.addPoint(desiredOutputs[0], actualOutputs[0], false);
				}
			}
			
			// An ArrayList of the pairs of doubles
			ArrayList<Pair<Double, Double>> neuronResults = new ArrayList<Pair<Double, Double>>(n.numOutputs());
			for (int i = 0; i < desiredOutputs.length; i++) {
				// Add all of the pairs of desired and actual outputs to compare
				assert!Double.isNaN(desiredOutputs[i]) : "desiredOutputs[" + i + "] is NaN!";
				assert!Double.isNaN(actualOutputs[i]) : "actualOutputs[" + i + "] is NaN!";
				// Add the desired outputs and actual outputs of the pixel as a pair 
				neuronResults.add(new Pair<Double, Double>(desiredOutputs[i], actualOutputs[i]));
			}
			// add all of the pairs of desired and actual outputs to compare
			samples.add(neuronResults);
			if (CommonConstants.watch && pauseForEachCase) {
				MiscUtil.waitForReadStringAndEnterKeyPress();
			}
		}
		
		if(display != null) {
			display.clear();
			System.out.println("Press enter");
			MiscUtil.waitForReadStringAndEnterKeyPress();
		}
		
		// find the average amount/occurrence of error between each desired and
		// actual output pairs put into the samples
		double averageError = StatisticsUtilities.averageSquaredErrorEnergy(samples);
		assert!Double.isNaN(averageError) : "averageError is NaN!";
		return averageError;
	}

	/**
	 * Finds the number of inputs for the pair for the network to evolve and
	 * match
	 * 
	 * @return the number of inputs
	 */
	public abstract int numInputs();

	/**
	 * Finds the number of outputs for the pair for the network to evolve and
	 * match
	 * 
	 * @return the number of outputs
	 */
	public abstract int numOutputs();

	/**
	 * Just the approximation error
	 * 
	 * @return
	 */
	public int numObjectives() {
		return 1;
	}

	/**
	 * returns the timeStamp, which is 0 by default
	 */
	public double getTimeStamp() {
		return 0;
	}

	/**
	 * Get or generate a collection of desired input/output pairs to train on.
	 * 
	 * @return collection of Pairs
	 */
	public abstract ArrayList<Pair<double[], double[]>> getTrainingPairs();

	@Override
	public void postConstructionInitialization() {
		MMNEAT.setNNInputParameters(numInputs(), numOutputs());
	}
}
