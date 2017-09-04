/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.southwestern.tasks.testmatch.functions;

import edu.southwestern.networks.Network;
import edu.southwestern.tasks.testmatch.MatchDataTask;
import edu.southwestern.util.datastructures.Pair;
import java.util.ArrayList;

/**
 * Simple XOR task
 * 
 * @author Jacob Schrum
 */
public class XORTask<T extends Network> extends MatchDataTask<T> {

	@Override
	public ArrayList<Pair<double[], double[]>> getTrainingPairs() {
		ArrayList<Pair<double[], double[]>> pairs = new ArrayList<Pair<double[], double[]>>();
		pairs.add(new Pair<double[], double[]>(new double[] { 0, 0 }, new double[] { 0 }));
		pairs.add(new Pair<double[], double[]>(new double[] { 0, 1 }, new double[] { 1 }));
		pairs.add(new Pair<double[], double[]>(new double[] { 1, 0 }, new double[] { 1 }));
		pairs.add(new Pair<double[], double[]>(new double[] { 1, 1 }, new double[] { 0 }));
		return pairs;
	}

	public String[] sensorLabels() {
		return new String[] { "Bit 0", "Bit 1" };
	}

	public String[] outputLabels() {
		return new String[] { "XOR" };
	}

	@Override
	public int numInputs() {
		return 2;
	}

	@Override
	public int numOutputs() {
		return 1;
	}

}
