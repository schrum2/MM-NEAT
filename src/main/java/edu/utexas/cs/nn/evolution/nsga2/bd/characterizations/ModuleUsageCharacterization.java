package edu.utexas.cs.nn.evolution.nsga2.bd.characterizations;

import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.nsga2.bd.vectors.BehaviorVector;
import edu.utexas.cs.nn.evolution.nsga2.bd.vectors.RealBehaviorVector;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

/**
 * Gets a behavior vector based on module usage
 * 
 * @author Jacob Schrum
 * @commented Lauren Gillespie
 * @param <TWEANN>
 */
public class ModuleUsageCharacterization<TWEANN> implements BehaviorCharacterization<TWEANN> {

	/**
	 * gets a behavior vector that is measured using module usage
	 * 
	 * @param score
	 *            raw score
	 */
	@Override
	public BehaviorVector getBehaviorVector(Score<TWEANN> score) {
		return new RealBehaviorVector(
				StatisticsUtilities.distribution(((TWEANNGenotype) score.individual).getModuleUsage()));
	}

	/**
	 * prepares for getting behavior vector by creating random testing syllabus
	 */
	@Override
	public void prepare() {
	}
}
