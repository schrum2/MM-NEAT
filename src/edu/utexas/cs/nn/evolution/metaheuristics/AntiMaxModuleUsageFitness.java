package edu.utexas.cs.nn.evolution.metaheuristics;

import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.scores.Score;

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
