package edu.southwestern.networks;

import edu.southwestern.parameters.CommonConstants;

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
		assert(CommonConstants.leo || Math.abs(originalOutput) > CommonConstants.linkExpressionThreshold) : "This link should not be expressed: " + originalOutput;
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
	public static double[] propagateOneStep(double[] fromLayer, double[] toLayer, double[][] connections) {
		for (int from = 0; from < fromLayer.length; from++) {
			for (int to = 0; to < toLayer.length; to++) {
				toLayer[to] += fromLayer[from] * connections[from][to];
			}
		}
		return toLayer;
	}

	/**
	 * Propagates values forward one step  by multiplying value at first layer by
	 * connection weight between layers and setting target layer equal to this value
	 * @param fromLayer source layer
	 * @param toLayer target layer (modified)
	 * @param connections connections between the two layers
	 */
	public static void propagateOneStep(double[][] fromLayer, double[][] toLayer, double[][][][] connections) {
		assert(connections.length * connections[0].length == fromLayer.length * fromLayer[0].length):"from layer size doesn't match size of connections!";
		assert(connections[0][0].length * connections[0][0][0].length == toLayer.length * toLayer[0].length):"to layer size doesn't match size of connections!";
		for (int X1 = 0; X1 < connections.length; X1++) {
			for(int Y1 = 0; Y1 < connections[0].length; Y1++) {
				for(int X2 = 0; X2 < connections[0][0].length; X2++) {
					for(int Y2 = 0; Y2 < connections[0][0][0].length; Y2++) {
						toLayer[X2][Y2] += fromLayer[X1][Y1] * connections[X1][Y1][X2][Y2];
					}
				}
			}
		}
	}

	/**
	 * Activate all neurons in layer with specified activation function
	 * @param nodes 2D array of pre-activated neuron sums
	 * @param ftype Valid activation function type in ActivationFunctions
	 */
	public static void activateLayer(double[][] nodes, int ftype) {
		for (int i = 0; i < nodes.length; i++) {
			for (int j = 0; j < nodes[0].length; j++) {
				nodes[i][j] = ActivationFunctions.activation(ftype, nodes[i][j]);
			}
		}
	}
}
