package edu.utexas.cs.nn.scores;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This is a class that keeps track of an agent's score, the number of
 * evaluations performed on it and the behaviors available to the agent. It has
 * multiple getter methods and actions to be performed on the score.
 *
 * @author Jacob Schrum
 */
public class Score<T> {

	public int evals;// number of evals performed on this agent's score
	public double[] scores;// array of different scores agent acquired in
							// successive runs
	public double[] otherStats;// array of other stats from domain pertinent to
								// score
	public double totalEvalTime = -1;// not sure what this is
	public double averageEvalTime = -1;// not sure what this is
	public Genotype<T> individual;// the genotype of the individual in question
	public ArrayList<Double> behaviorVector;// the behavior actions available to
											// the individual

	/**
	 * Default constructor for Score object.
	 * 
	 * @param individual:
	 *            Genotype of the individual in question
	 * @param scores:
	 *            array of all other scores of similar agents in the domain
	 * @param behaviorVector:
	 *            an ArrayList of possible behaviors of the agent
	 */
	public Score(Genotype<T> individual, double[] scores, ArrayList<Double> behaviorVector) {
		this(individual, scores, behaviorVector, new double[0]);
	}

	/**
	 * Default constructor for Score object if other stats are known.
	 * 
	 * @param individual:
	 *            Genotype of the individual in question
	 * @param scores:
	 *            array of all other scores of similar agents in the domain
	 * @param behaviorVector:
	 *            an ArrayList of possible behaviors of the agent
	 * @param otherStats:
	 *            a double array containing other stats from the domain that are
	 *            relevant to the score.
	 */
	public Score(Genotype<T> individual, double[] scores, ArrayList<Double> behaviorVector, double[] otherStats) {
		this(individual, scores, behaviorVector, otherStats, 1);
	}

	/**
	 * Constructor for Score object if all parameters known.
	 * 
	 * @param individual:
	 *            Genotype of the individual in question
	 * @param scores:
	 *            array of all other scores of similar agents in the domain
	 * @param behaviorVector:
	 *            an ArrayList of possible behaviors of the agent
	 * @param otherStats:
	 *            a double array containing other stats from the domain that are
	 *            relevant to the score.
	 * @param evals:
	 *            number of evaluations of the score to be performed.
	 */

	public Score(Genotype<T> individual, double[] scores, ArrayList<Double> behaviorVector, double[] otherStats,
			int evals) {
		this.evals = evals;
		this.individual = individual;
		this.scores = scores;
		this.otherStats = otherStats;
		this.behaviorVector = behaviorVector;
	}

	/**
	 * Given two Score instances from the same task, add the scores and other
	 * stats of other to the scores and other stats of this score instance to
	 * create a new Score instance (with this Genotype) which is returned.
	 *
	 * @param other
	 *            other Score instance from same task with same scores and
	 *            otherStats
	 * @return sum Score instance
	 */
	public Score<T> add(Score<T> other) {
		if (other == null) {
			return this.copy();
		}

		assert(this.scores.length == other.scores.length);
		assert(this.otherStats.length == other.otherStats.length);

		Score<T> result = new Score<T>(individual, ArrayUtil.zipAdd(scores, other.scores), behaviorVector,
				ArrayUtil.zipAdd(otherStats, other.otherStats));
		result.evals = this.evals + other.evals;
		return result;
	}

	/**
	 * Divides the score by a value, x.
	 * 
	 * @param x:
	 *            the double by which the score is divided. A unique math
	 *            'trick' was used to make this work, by dividing 1 by x and
	 *            then multiplying the score in each index of the score array by
	 *            that fraction to prevent code-crashing errors, such as
	 *            dividing by 0
	 * @return: returns the score after dividing it
	 */
	public Score<T> divide(double x) {
		Score<T> result = new Score<T>(individual, ArrayUtil.scale(scores, 1.0 / x), behaviorVector,
				ArrayUtil.scale(otherStats, 1.0 / x));
		return result;
	}

	/**
	 * Average another Score instance with this score instance, using the evals
	 * variable to appropriately weight the contribution of this and other to
	 * form a common average.
	 *
	 * @param other
	 *            Other score instance, possibly null (treated as zero eval)
	 * @return new Score that is the average of this and other
	 */
	public Score<T> incrementalAverage(Score<T> other) {
		if (other == null) {
			return this.copy();
		}
		assert(this.scores.length == other.scores.length);
		assert(this.otherStats.length == other.otherStats.length);

		double[] thisWeightedScores = ArrayUtil.scale(this.scores, (this.evals * 1.0) / (this.evals + other.evals));
		double[] otherWeightedScores = ArrayUtil.scale(other.scores, (other.evals * 1.0) / (this.evals + other.evals));
		double[] scoresAvg = ArrayUtil.zipAdd(thisWeightedScores, otherWeightedScores);

		double[] thisWeightedOtherStats = ArrayUtil.scale(this.otherStats,
				(this.evals * 1.0) / (this.evals + other.evals));
		double[] otherWeightedOtherStats = ArrayUtil.scale(other.otherStats,
				(other.evals * 1.0) / (this.evals + other.evals));
		double[] otherStatsAvg = ArrayUtil.zipAdd(thisWeightedOtherStats, otherWeightedOtherStats);

		Score<T> result = new Score<T>(individual, scoresAvg, behaviorVector, otherStatsAvg);
		result.evals = this.evals + other.evals;
		return result;
	}

	// Copies the score.
	public Score<T> copy() {
		return new Score<T>(individual, Arrays.copyOf(scores, scores.length), behaviorVector,
				Arrays.copyOf(otherStats, otherStats.length));
	}

	// Getter method for number of previous scores calculated for agent.
	public int numObjectives() {
		return scores.length;
	}

	// Determines if agent score is better than other.
	public boolean isBetter(Score<T> other) {
		return scores[0] > other.scores[0];
	}

	// Determines if agent score is better or if equal.
	public boolean isAtLeastAsGood(Score<T> other) {
		return scores[0] >= other.scores[0];
	}

	// Determines if agent score is worse than other.
	public boolean isWorse(Score<T> other) {
		return scores[0] < other.scores[0];
	}

	// Adds a new score to score array.
	public void extraScore(double score) {
		double[] newScores = new double[scores.length + 1];
		System.arraycopy(scores, 0, newScores, 0, scores.length);
		newScores[scores.length] = score;
		scores = newScores;
	}

	/**
	 * Delete the score from the last objective from the list of scores, which
	 * also decreases the number of objectives
	 */
	public void dropLastScore() {
		double[] newScores = new double[scores.length - 1];
		System.arraycopy(scores, 0, newScores, 0, newScores.length);
		scores = newScores;
	}

	// Prints the contents of the agent's data to the console.
	public String toString() {
		return (individual == null ? "NULL" : individual.getId()) + ":N=" + evals + ":" + Arrays.toString(scores) + (otherStats != null && otherStats.length > 0 ? Arrays.toString(otherStats) : "");
	}

	// allows behaviorVector to be printed and then set to a new behavoirVector
	public void giveBehaviorVector(ArrayList<Double> behaviorVector) {
		if (behaviorVector != null) {
			System.out.println("Behavior ArrayList: " + behaviorVector);
		}
		this.behaviorVector = behaviorVector;
	}

	/**
	 * maxScores finds the largest score and other stats of both agents. If one
	 * of the scores is null, it simply returns the other score. Else, it
	 * returns a new Score object that contains the biggest score and otherStats
	 * between the two agents. Finally, it incrememnts the number of evals
	 * performed on the specific agent.
	 * 
	 * @param other:
	 *            The score of another agent.
	 * @return:The largest of the scores and other stats between the two scores.
	 */
	public Score<T> maxScores(Score<T> other) {
		if (other == null) {
			return this.copy();
		}
		assert(this.scores.length == other.scores.length);
		assert(this.otherStats.length == other.otherStats.length);

		Score<T> result = new Score<T>(individual, ArrayUtil.zipMax(this.scores, other.scores), behaviorVector,
				ArrayUtil.zipMax(this.otherStats, other.otherStats));
		result.evals = this.evals + other.evals;
		return result;
	}
	
	/**
	 * replaces old scores with new scores
	 * @param newScores
	 */
	public void replaceScores(double[] newScores){
		scores = newScores;
	}
}
