package edu.southwestern.evolution.nsga2.bd.characterizations;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.nsga2.bd.vectors.BehaviorVector;
import edu.southwestern.evolution.nsga2.bd.vectors.RealBehaviorVector;
import edu.southwestern.scores.Score;
import edu.southwestern.util.stats.StatisticsUtilities;

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

	@Override
	public ArrayList<double[]> getSyllabus() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ArrayList<BehaviorVector> getAllBehaviorVectors(ArrayList<Score<TWEANN>> population,
			BehaviorCharacterization characterization) {
		// TODO Auto-generated method stub
		return null;
	}
}
