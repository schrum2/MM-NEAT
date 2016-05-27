package edu.utexas.cs.nn.evolution.metaheuristics;

import edu.utexas.cs.nn.evolution.genotypes.NetworkGenotype;
import edu.utexas.cs.nn.scores.Score;

/**
 * Fitness that encourages having lots of modes. Only makes sense if some other
 * mechanism is limiting mode mutations.
 *
 * @author Jacob Schrum
 */
public class MaxModulesFitness implements Metaheuristic {

	@SuppressWarnings("rawtypes")
	@Override
	public void augmentScore(Score s) {
		s.extraScore(((NetworkGenotype) s.individual).numModules());
	}

	@Override
	public double minScore() {
		return 1;
	}

	@Override
	public double startingTUGGoal() {
		return minScore();
	}
}
