package edu.southwestern.networks.dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * Basically a neural network, but has tensors as input and output,
 * represented by DL4J's INDArray class.
 * @author Jacob Schrum
 */
public interface TensorNetwork {
	public INDArray output(INDArray input);
}
