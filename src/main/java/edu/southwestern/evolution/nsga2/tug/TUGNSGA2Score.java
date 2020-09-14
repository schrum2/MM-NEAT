package edu.southwestern.evolution.nsga2.tug;

import edu.southwestern.evolution.nsga2.NSGA2Score;
import edu.southwestern.scores.Score;
import edu.southwestern.util.datastructures.ArrayUtil;
import java.util.Arrays;

/**
 * Same as NSGA2 score, but removes inactive objectives when comparing
 *
 * @author Jacob Schrum
 */
public class TUGNSGA2Score<T> extends NSGA2Score<T> {

	private final boolean[] active;

	/**
	 * Creates an instance of TUGNSGA2Score
	 * @param s, the Score
	 * @param active, a boolean array of whether or not the Scores for each objective are active
	 */
	public TUGNSGA2Score(Score<T> s, boolean[] active) {
		super(s.individual, s.scores, s.usesTraditionalBehaviorVector() ? s.getTraditionalDomainSpecificBehaviorVector() : null, s.otherStats);
		this.active = active;
	}

	@Override
	/**
	 * A toString method to print out the TUGNSGA2Score
	 */
	public String toString() {
		return "ALL:" + super.toString() + ":FILTERED BY: " + Arrays.toString(active) + " = "
				+ Arrays.toString(filterByGoals(scores, active));
	}

	/**
	 * Counts the number of active scores after TUG influence
	 * @param scores, the scores in an array of doubles
	 * @param active, which objectives are active and which aren't, in a boolean array.
	 * 		value is true if the objective is active, false if not
	 * @return reducedObjectives in an array of doubles
	 */
	private static double[] filterByGoals(double[] scores, boolean[] active) {
		int activeObjectives = ArrayUtil.countOccurrences(true, active);
		double[] reducedObjectives = new double[activeObjectives];
		int index = 0;
		for (int i = 0; i < active.length; i++) {
			if (active[i]) {
				reducedObjectives[index++] = scores[i];
			}
		}
		// System.out.println("\t" + Arrays.toString(scores) + " reduced to " +
		// Arrays.toString(reducedObjectives) + " based on " +
		// Arrays.toString(active));
		return reducedObjectives;
	}

	@Override
	/**
	 * Finds whether or not this score is better than the score given as a parameter
	 * @param other, the other Score
	 * @return true if this Score is better than the other Score, false if not
	 */
	public boolean isBetter(Score<T> other) {
		NSGA2Score<T> lhs = new NSGA2Score<T>(individual, filterByGoals(scores, active), null, otherStats);
		NSGA2Score<T> rhs = new NSGA2Score<T>(other.individual, filterByGoals(other.scores, active), null, other.otherStats);
		return lhs.isBetter(rhs);
	}

	@Override
	/**
	 * Finds whether or not this score is worse than the score given as a parameter
	 * @param other, the other Score
	 * @return true if this Score is worse than the other Score, false if not
	 */
	public boolean isWorse(Score<T> other) {
		NSGA2Score<T> lhs = new NSGA2Score<T>(individual, filterByGoals(scores, active), null, otherStats);
		NSGA2Score<T> rhs = new NSGA2Score<T>(other.individual, filterByGoals(other.scores, active), null, other.otherStats);
		return lhs.isWorse(rhs);
	}

	@Override
	/**
	 * Checks to see if a certain objective is active or not (if it is being used)
	 * @param the objective, as an int (the index in the list of objectives)
	 * @return true if the objective is active (being used), false if not
	 */
	public boolean useObjective(int objective) {
		return active[objective];
	}
}
