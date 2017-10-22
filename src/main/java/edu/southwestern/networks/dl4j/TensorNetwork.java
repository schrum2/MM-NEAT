package edu.southwestern.networks.dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * Basically a neural network, but has tensors as input and output,
 * represented by DL4J's INDArray class.
 * @author Jacob Schrum
 */
public interface TensorNetwork {
	/**
	 * output for given input
	 * @param input Tensor input that matches network input shape
	 * @return corresponding output
	 */
	public INDArray output(INDArray input);
	/** 
	 * eliminate recurrent state
	 */
	public void flush();
	/**
	 * Train using backprop. Each row of the input has as its
	 * expected output each corresponding row of targets.
	 * 
	 * @param input Input examples
	 * @param labels Corresponding known output targets
	 */
	public void fit(INDArray input, INDArray targets);
}
