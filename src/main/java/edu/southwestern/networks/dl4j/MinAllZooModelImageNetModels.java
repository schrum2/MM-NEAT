package edu.southwestern.networks.dl4j;

import java.util.Iterator;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.api.ndarray.INDArray;

import edu.southwestern.util.datastructures.ArrayUtil;

public class MinAllZooModelImageNetModels extends AllZooModelImageNetModels {

	/**
	 * For the given inputs, apply all models, get the minimum in each category.
	 */
	@Override
	public INDArray output(INDArray input) {
		Iterator<String> itr = imageNetModels.keySet().iterator();
		ComputationGraph model = imageNetModels.get(itr.next());
		INDArray scores = model.output(input)[0]; // First set of scores
		while(itr.hasNext()) {
			model = imageNetModels.get(itr.next());
			INDArray nextScores = model.output(input)[0];
			// Get minimum in each category
			ArrayUtil.pairwiseMinimum(scores, nextScores); // Modifies scores variable
		}
		// scores is now the element-wise min
		return scores;
	}	
}
