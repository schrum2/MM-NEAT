package edu.southwestern.networks.dl4j;

import java.io.IOException;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * This is a simple facade for sending input to a model from 
 * DL4J's model zoo. The benefit is that it uses the TensorNetwork
 * interface, which can be used by other non-zoo network types.
 * @author Jacob Schrum
 */
public class ZooModelImageNetWrapper implements TensorNetwork {

	ComputationGraph graph;
	
	public ZooModelImageNetWrapper(@SuppressWarnings("rawtypes") ZooModel model) {
		try {
			graph = (ComputationGraph) model.initPretrained(PretrainedType.IMAGENET);
		} catch (IOException e) {
			System.out.println("Could not init model " + model);
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@Override
	public INDArray output(INDArray input) {
		// I'm not fully clear why an array of INDArrays is returned, but
		// the ImageNet examples return index 0 for some reason.
		return graph.output(input)[0];
	}

	
}
