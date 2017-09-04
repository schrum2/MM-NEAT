package edu.southwestern.evolution.metaheuristics;

import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;

/**
 * Having excessive links per network module is penalized.
 * If penalizeLinksPerMode is false, then links in general
 * are penalized (ignoring modules)
 *
 * @author Jacob Schrum
 */
public class LinkPenalty implements Metaheuristic<TWEANN> {

	private final int populationIndex;
	private final boolean modeAvg;

	public LinkPenalty() {
		this(0);
	}

	public LinkPenalty(int populationIndex) {
		this.populationIndex = populationIndex;
		this.modeAvg = Parameters.parameters.booleanParameter("penalizeLinksPerMode");
	}

	public void augmentScore(Score<TWEANN> s) {
		s.extraScore(getScore((TWEANNGenotype) s.individual));
	}

	public double minScore() {
		int nodes = EvolutionaryHistory.archetypeSize(populationIndex);
		return -(nodes * nodes); // Every node connected to every other
	}

	public double startingTUGGoal() {
		return minScore();
	}

	public double getScore(TWEANNGenotype g) {
		return -(g.links.size() / (1.0 * (modeAvg ? g.numModules : 1)));
	}
}
