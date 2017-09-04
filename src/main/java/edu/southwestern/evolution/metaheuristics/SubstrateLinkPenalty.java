package edu.utexas.cs.nn.evolution.metaheuristics;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATUtil;
import edu.utexas.cs.nn.scores.Score;

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
		return HyperNEATUtil.totalPossibleLinks(hnt);
	}

	public double startingTUGGoal() {
		return minScore();
	}

	public double getScore(HyperNEATCPPNGenotype g) {
		return -g.getSubstrateGenotype(hnt).links.size();
	}
}
