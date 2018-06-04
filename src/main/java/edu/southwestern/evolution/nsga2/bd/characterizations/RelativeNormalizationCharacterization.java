package edu.southwestern.evolution.nsga2.bd.characterizations;

import java.util.ArrayList;

import edu.southwestern.evolution.nsga2.bd.vectors.BehaviorVector;
import edu.southwestern.evolution.nsga2.bd.vectors.RealBehaviorVector;
import edu.southwestern.networks.Network;
import edu.southwestern.scores.Score;
import edu.southwestern.util.stats.StatisticsUtilities;

public class RelativeNormalizationCharacterization<T extends Network> extends GeneralNetworkCharacterization<T> {

	/**
	 * creates normalized behavior vector
	 * @param raw score
	 * @return normalized behavior vector
	 */
	@Override
	public BehaviorVector getBehaviorVector(Score<T> score) {
		ArrayList<Double> original = getBehaviorVector(score.individual.getPhenotype(), this.syllabus);
		Double max = original.get(StatisticsUtilities.argmax(original));
		for (Double element: original) {
			element /= max; 
		}
		return new RealBehaviorVector(original);
	}
}
