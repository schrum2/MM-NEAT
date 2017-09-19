package edu.southwestern.networks.dl4j;

import java.util.Iterator;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.api.ndarray.INDArray;

public class ConcatAllZooModelImageNetModels extends AllZooModelImageNetModels {

	/**
	 * Combine all categories across all models into long array
	 */
	@Override
	public INDArray output(INDArray input) {
		Iterator<String> itr = imageNetModels.keySet().iterator();
		ComputationGraph model = imageNetModels.get(itr.next());
		INDArray scores = model.output(input)[0]; // First set of scores
		while(itr.hasNext()) {
			model = imageNetModels.get(itr.next());
			INDArray nextScores = model.output(input)[0];
			// Append the next batch of scores
			// TODO
		}
		return null; // scores;
	}	
}
