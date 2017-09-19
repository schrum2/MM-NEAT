package edu.southwestern.networks.dl4j;

import java.util.Iterator;
import java.util.Map;

import org.nd4j.linalg.api.ndarray.INDArray;

public class ConcatAllZooModelImageNetModels extends AllZooModelImageNetModels {

	/**
	 * Combine all categories across all models into long array
	 */
	@Override
	public INDArray output(INDArray input) {
		Map<String,INDArray> allScores = AllZooModelImageNetModels.runAllModels(input);
		Iterator<String> itr = allScores.keySet().iterator();
		INDArray scores = allScores.get(itr.next()); // these scores will be destructively modified
		while(itr.hasNext()) {
			INDArray nextScores = allScores.get(itr.next());
			// Append the next batch of scores
			// TODO
		}
		return null; // scores;
	}	
}
