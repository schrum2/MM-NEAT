/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.testmatch.functions;

import edu.southwestern.networks.Network;
import edu.southwestern.tasks.testmatch.MatchDataTask;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.stats.StatisticsUtilities;
import java.util.ArrayList;

/**
 * Argmax
 *
 * @author Jacob Schrum
 */
public class ArgMaxTask<T extends Network> extends MatchDataTask<T> {

	private final int numArgs;
	private final int samples;

	public ArgMaxTask() {
		this(2, 2000);
	}

	public ArgMaxTask(int num, int samples) {
		this.numArgs = num;
		this.samples = samples;
	}

	@Override
	public ArrayList<Pair<double[], double[]>> getTrainingPairs() {
		ArrayList<Pair<double[], double[]>> pairs = new ArrayList<Pair<double[], double[]>>(2 * samples);
		for (int i = 0; i < samples; i++) {
			double[] inputs = RandomNumbers.randomArray(numInputs());
			// if(numArgs == 2){ // Make the two values very different
			// inputs[2] = -inputs[0];
			// }
			double[] outputs = resultsForInput(inputs);
			pairs.add(new Pair<double[], double[]>(inputs, outputs));
		}
		return pairs;
	}

	public double[] resultsForInput(double[] inputs) {
		double[] preferences = new double[numArgs];
		for (int j = 1; j < inputs.length; j += 2) {
			preferences[j / 2] = inputs[j];
		}
		int answer = StatisticsUtilities.argmax(preferences);
		return new double[] { inputs[answer * 2] };
	}

	public String[] sensorLabels() {
		String[] result = new String[numInputs()];
		for (int i = 0; i < result.length; i += 2) {
			result[i] = "Argument " + i;
			result[i + 1] = "Value " + i;
		}
		return result;
	}

	public String[] outputLabels() {
		return new String[] { "Max Argument" };
	}

	@Override
	public int numInputs() {
		return numArgs * 2;
	}

	@Override
	public int numOutputs() {
		return 1;
	}
}
