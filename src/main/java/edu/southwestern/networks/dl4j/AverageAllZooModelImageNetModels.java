package edu.southwestern.networks.dl4j;

import java.util.Iterator;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.api.ndarray.INDArray;

public class AverageAllZooModelImageNetModels extends AllZooModelImageNetModels {

	/**
	 * For the given inputs, apply all models, add up the scores, and
	 * then average them.
	 */
	@Override
	public INDArray output(INDArray input) {
		Iterator<String> itr = imageNetModels.keySet().iterator();
		ComputationGraph model = imageNetModels.get(itr.next());
		INDArray scores = model.output(input)[0]; // First set of scores
		while(itr.hasNext()) {
			model = imageNetModels.get(itr.next());
			INDArray nextScores = model.output(input)[0];
			// Add up the scores
			scores = scores.add(nextScores);
		}
		// scores is now the element-wise sum
		return scores.div(imageNetModels.size()); // Return element-wise average
	}
	
	
	
}
