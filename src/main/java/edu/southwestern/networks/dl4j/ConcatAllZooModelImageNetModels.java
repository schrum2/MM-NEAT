package edu.southwestern.networks.dl4j;

import java.util.Iterator;
import java.util.Map;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

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
			scores = Nd4j.concat(1, scores, nextScores);
		}
//		System.out.println(scores.length() + ":" + scores.size(0) + ":" + scores.size(1));
//		System.out.println(scores);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		return scores;
	}	
}
