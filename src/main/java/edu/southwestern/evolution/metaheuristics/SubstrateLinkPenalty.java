package edu.southwestern.evolution.metaheuristics;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.HyperNEATUtil;
import edu.southwestern.scores.Score;

/**
 * Having excessive links in the substrate network of
 * a HyperNEAT run is penalized
 *
 * @author Jacob Schrum
 */
public class SubstrateLinkPenalty implements Metaheuristic<TWEANN> {

	private HyperNEATTask hnt;

	public SubstrateLinkPenalty() {
		this.hnt = (HyperNEATTask) MMNEAT.task;
	}

	public void augmentScore(Score<TWEANN> s) {
		s.extraScore(getScore((HyperNEATCPPNGenotype) s.individual));
	}

	public double minScore() {
		return -HyperNEATUtil.totalPossibleLinks(hnt);
	}

	public double startingTUGGoal() {
		return minScore();
	}

	public double getScore(HyperNEATCPPNGenotype g) {
		return -g.getSubstrateGenotype(hnt).links.size();
	}
}
