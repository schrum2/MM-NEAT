package edu.utexas.cs.nn.evolution.ucb;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import java.util.Comparator;

/**
 * Use UCB1 method to determine which of two scores has the highest Upper
 * Confidence Bound, and therefore deserves further evaluations.
 *
 * @author Jacob Schrum
 * @param <T> phenotype
 */
public class UCB1Comparator<T> implements Comparator<Score<T>> {

	private final int index;
	private int numTotalLeverPulls;
	private double maxReward;
	private final double explorePreference;

	public UCB1Comparator(int index, int numTotalLeverPulls, double maxReward) {
		this.index = index;
		this.numTotalLeverPulls = numTotalLeverPulls;
		this.maxReward = maxReward;
		this.explorePreference = Parameters.parameters.doubleParameter("explorePreference");
	}

	public void setMax(double reward) {
		maxReward = Math.max(reward, maxReward);
		// System.out.println("MAX:"+maxReward);
	}

	public void increaseTotal() {
		numTotalLeverPulls++;
	}

        @Override
	public int compare(Score<T> o1, Score<T> o2) {
		double o1ucb1Score = ucb1(o1);
		double o2ucb1Score = ucb1(o2);
		double diff = o1ucb1Score - o2ucb1Score;
		return (int) Math.signum(diff);
	}

	public double ucb1(Score<T> s) {
		return s.evals >= CommonConstants.maxTrials ? -1
				: (s.scores[index] / maxReward)
						+ explorePreference * Math.sqrt((2 * Math.log(numTotalLeverPulls)) / s.evals);
	}
}
