package edu.southwestern.networks.dl4j;

import java.util.Iterator;
import java.util.Map;

import org.nd4j.linalg.api.ndarray.INDArray;

import edu.southwestern.util.datastructures.ArrayUtil;

public class MinAllZooModelImageNetModels extends AllZooModelImageNetModels {

	/**
	 * For the given inputs, apply all models, get the minimum in each category.
	 */
	@Override
	public INDArray output(INDArray input) {
		Map<String,INDArray> allScores = AllZooModelImageNetModels.runAllModels(input);
		Iterator<String> itr = allScores.keySet().iterator();
		INDArray scores = allScores.get(itr.next()); // these scores will be destructively modified
		while(itr.hasNext()) {
			INDArray nextScores = allScores.get(itr.next());
			// Get minimum in each category
			ArrayUtil.pairwiseMinimum(scores, nextScores); // Modifies scores variable
		}
		// scores is now the element-wise min
		return scores;
	}	
}
