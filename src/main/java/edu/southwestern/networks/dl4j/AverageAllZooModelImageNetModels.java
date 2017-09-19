package edu.southwestern.networks.dl4j;

import java.util.Iterator;
import java.util.Map;

import org.nd4j.linalg.api.ndarray.INDArray;

public class AverageAllZooModelImageNetModels extends AllZooModelImageNetModels {

	/**
	 * For the given inputs, apply all models, add up the scores, and
	 * then average them.
	 */
	@Override
	public INDArray output(INDArray input) {
		Map<String,INDArray> allScores = AllZooModelImageNetModels.runAllModels(input);
		Iterator<String> itr = allScores.keySet().iterator();
		INDArray scores = allScores.get(itr.next()); // these scores will be destructively modified
		while(itr.hasNext()) {
			INDArray nextScores = allScores.get(itr.next());
			// Add up the scores
			scores = scores.add(nextScores);
		}
		// scores is now the element-wise sum
		return scores.div(allScores.size()); // Return element-wise average
	}
	
	
	
}
