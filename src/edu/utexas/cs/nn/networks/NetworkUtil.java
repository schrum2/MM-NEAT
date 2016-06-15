package edu.utexas.cs.nn.networks;

import edu.utexas.cs.nn.parameters.CommonConstants;

/**
 * Contains various util functions for netwokrs
 * @author Lauren Gillespie
 *
 */
public class NetworkUtil {

	/**
	 * Used for standard HyperNEAT link expression. If a link is to be
	 * expressed, then values beyond a threshold slide back to 0 so that weights
	 * with a small magnitude are possible.
	 *
	 * @param originalOutput
	 *            original CPPN output
	 * @return Scaled synaptic weight
	 */
	public static double calculateWeight(double originalOutput) {
		assert(Math.abs(originalOutput) > CommonConstants.linkExpressionThreshold) : "This link should not be expressed: " + originalOutput;
		if (originalOutput > CommonConstants.linkExpressionThreshold) {
			return originalOutput - CommonConstants.linkExpressionThreshold;
		} else {
			return originalOutput + CommonConstants.linkExpressionThreshold;
		}
	}
	
	/**
	 * Propagates values forward one step  by multiplying value at first layer by
	 * connection weight between layers and setting target layer equal to this value
	 * @param fromLayer source layer
	 * @param toLayer target layer
	 * @param connections connections between the two layers
	 */
	public static void propagateOneStep(double[] fromLayer, double[] toLayer, double[][] connections) {
		for (int from = 0; from < fromLayer.length; from++) {
			for (int to = 0; to < toLayer.length; to++) {
				toLayer[to] += fromLayer[from] * connections[from][to];
			}
		}
	}
}
