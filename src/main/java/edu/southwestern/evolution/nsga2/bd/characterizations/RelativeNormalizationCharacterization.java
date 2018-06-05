package edu.southwestern.evolution.nsga2.bd.characterizations;

import java.util.ArrayList;
import edu.southwestern.evolution.nsga2.bd.vectors.VectorUtil;
import edu.southwestern.evolution.nsga2.bd.vectors.BehaviorVector;
import edu.southwestern.evolution.nsga2.bd.vectors.RealBehaviorVector;
import edu.southwestern.networks.Network;
import edu.southwestern.scores.Score;

public class RelativeNormalizationCharacterization<T extends Network> extends GeneralNetworkCharacterization<T> {

	/**
	 * creates normalized behavior vector
	 * @param raw score
	 * @return normalized behavior vector
	 */
	@Override
	public BehaviorVector getBehaviorVector(Score<T> score) {
		ArrayList<Double> original = getBehaviorVector(score.individual.getPhenotype(), this.syllabus);
		Double max = VectorUtil.maxAbsoluteValue(original).t2;
		for (int i = 0; i < original.size(); i++) {
			original.set(i, original.get(i) / max);
		}
		return new RealBehaviorVector(original);
	}
}
