package edu.southwestern.evolution.metaheuristics;

import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.scores.Score;

/**
 * Punish overuse of a single module. Doesn't seem to work very well.
 *
 * @author Jacob Schrum
 */
public class AntiMaxModuleUsageFitness implements Metaheuristic<TWEANN> {

	public AntiMaxModuleUsageFitness() {
	}

	public void augmentScore(Score<TWEANN> s) {
		s.extraScore(getScore((TWEANNGenotype) s.individual));
	}

	public double minScore() {
		return -1;
	}

	public double startingTUGGoal() {
		return minScore();
	}

	public double getScore(TWEANNGenotype g) {
		return -g.maxModuleUsage();
	}
}
