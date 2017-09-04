package edu.southwestern.scores;

import edu.southwestern.evolution.genotypes.Genotype;
import java.util.ArrayList;

/**
 * This class stores and compares scores for an individual if it has multiple
 * objectives.
 *
 * @author Jacob Schrum
 */
public class MultiObjectiveScore<T> extends Score<T> {

	public static final double SMALL_DIFFERENCE = Double.MIN_VALUE;

	/**
	 * Default constructor for multiObjectiveScore
	 * 
	 * @param individual:
	 *            the genotype of the individual in question
	 * @param scores:a
	 *            double array containing the individual's past scores
	 * @param behaviorVector:
	 *            the behaviors open to the individual
	 */
	public MultiObjectiveScore(Genotype<T> individual, double[] scores, ArrayList<Double> behaviorVector) {
		this(individual, scores, behaviorVector, new double[0]);
	}

	/**
	 * Constructor for multiObjectiveScore. Inherits from the Score class.
	 * 
	 * @param individual:
	 *            the genotype of the individual in question
	 * @param scores:a
	 *            double array containing the individual's past scores
	 * @param behaviorVector:
	 *            the behaviors open to the individual
	 * @param otherStats:
	 *            a double array that contains other stats about the domain that
	 *            are pertinent to calculating the score
	 */
	public MultiObjectiveScore(Genotype<T> individual, double[] scores, ArrayList<Double> behaviorVector,
			double[] otherStats) {
		super(individual, scores, behaviorVector, otherStats);
	}

	// returns the score at the given index
	public double objectiveScore(int index) {
		return scores[index];
	}

	/**
	 * A method that determines if the individual's scores are better than
	 * another individual's scores
	 * 
	 * @param other:
	 *            the other individual's score
	 * 
	 * @return: returns a boolean corresponding to whether the individual's
	 *          scores are better than the other's. Returns true only if the
	 *          individual has more scores that are greater than the other's
	 *          scores. continues if the difference between the two scores is
	 *          smaller than small difference.
	 */
	public boolean isBetter(Score<T> other) {
		int betterObjectives = 0;// keeps track of which agent has the greater
									// scores.
		for (int i = 0; i < scores.length; i++) {
			if (Math.abs(scores[i] - other.scores[i]) < SMALL_DIFFERENCE) {
				// Practically equal
				continue;
			}
			if (scores[i] < other.scores[i]) {
				return false;
			}
			if (scores[i] > other.scores[i]) {
				betterObjectives++;
			}
		}
		return betterObjectives > 0;
	}

	/**
	 * A method that determines if the individual's scores are better than or
	 * equal to another.
	 * 
	 * @param other:
	 *            the other individual's score
	 * 
	 * @return: returns a boolean corresponding to whether the individual's
	 *          scores are better than or equal to the other's. Returns true if
	 *          the individual has more scores that are greater than the other's
	 *          scores or if both have an equal number of greater scores.
	 *          continues if the difference between the two scores is smaller
	 *          than small difference.
	 */
	public boolean isAtLeastAsGood(Score<T> other) {
		int betterObjectives = 0;// keeps track of which agent has the greater
									// scores.
		for (int i = 0; i < scores.length; i++) {
			if (Math.abs(scores[i] - other.scores[i]) < SMALL_DIFFERENCE) {
				// Practically equal
				continue;
			}
			if (scores[i] < other.scores[i]) {
				return false;
			}
			if (scores[i] > other.scores[i]) {
				betterObjectives++;
			}
		}
		return betterObjectives >= 0;
	}

	/**
	 * A method that determines if the individual's scores are worse than the
	 * other's.
	 * 
	 * @param other:
	 *            the other individual's score
	 * 
	 * @return: returns a boolean corresponding to whether the individual's
	 *          scores are worse than the other's. Returns true if all of the
	 *          individual's scores are worse than the other's.
	 */
	public boolean isWorse(Score<T> other) {
		int worseObjectives = 0;// keeps track of which agent has worse scores.
		for (int i = 0; i < scores.length; i++) {
			if (Math.abs(scores[i] - other.scores[i]) < SMALL_DIFFERENCE) {
				// Practically equal
				continue;
			}
			if (scores[i] > other.scores[i]) {
				return false;
			}
			if (scores[i] < other.scores[i]) {
				worseObjectives++;
			}
		}
		return worseObjectives > 0;
	}
}
