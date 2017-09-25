package edu.southwestern.networks.dl4j;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;

public class TensorNetworkFromMultiLayerNetwork implements TensorNetwork {

	private MultiLayerNetwork mln;

	public TensorNetworkFromMultiLayerNetwork(MultiLayerNetwork mln) {
		this.mln = mln;
	}
	
	@Override
	public INDArray output(INDArray input) {
		return mln.output(input);
	}

	@Override
	public void flush() {
		mln.rnnClearPreviousState();
	}

}
