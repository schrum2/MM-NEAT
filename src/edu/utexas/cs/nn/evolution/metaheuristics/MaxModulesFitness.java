package edu.utexas.cs.nn.evolution.metaheuristics;

import edu.utexas.cs.nn.evolution.genotypes.NetworkGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.scores.Score;

/**
 * Fitness that encourages having lots of modes. Only makes sense if some other
 * mechanism is limiting mode mutations.
 *
 * @author Jacob Schrum
 */
public class MaxModulesFitness<T extends Network> implements Metaheuristic<T> {

	@SuppressWarnings("rawtypes")
	@Override
	public void augmentScore(Score<T> s) {
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
