package edu.utexas.cs.nn.scores;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class Score<T> {

    public int evals;
    public double[] scores;
    public double[] otherStats;
    public double totalEvalTime = -1;
    public double averageEvalTime = -1;
    public Genotype<T> individual;
    public ArrayList<Double> behaviorVector;

    public Score(Genotype<T> individual, double[] scores, ArrayList<Double> behaviorVector) {
        this(individual, scores, behaviorVector, new double[0]);
    }

    public Score(Genotype<T> individual, double[] scores, ArrayList<Double> behaviorVector, double[] otherStats) {
        this(individual, scores, behaviorVector, otherStats, 1);
    }

    public Score(Genotype<T> individual, double[] scores, ArrayList<Double> behaviorVector, double[] otherStats, int evals) {
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
     * @param other other Score instance from same task with same scores and
     * otherStats
     * @return sum Score instance
     */
    public Score<T> add(Score<T> other) {
        if (other == null) {
            return this.copy();
        }

        assert (this.scores.length == other.scores.length);
        assert (this.otherStats.length == other.otherStats.length);

        Score<T> result = new Score<T>(individual, ArrayUtil.zipAdd(scores, other.scores), behaviorVector, ArrayUtil.zipAdd(otherStats, other.otherStats));
        result.evals = this.evals + other.evals;
        return result;
    }

    public Score<T> divide(double x) {
        Score<T> result = new Score<T>(individual, ArrayUtil.scale(scores, 1.0 / x), behaviorVector, ArrayUtil.scale(otherStats, 1.0 / x));
        return result;
    }

    /**
     * Average another Score instance with this score instance, using the evals
     * variable to appropriately weight the contribution of this and other to
     * form a common average.
     *
     * @param other Other score instance, possibly null (treated as zero eval)
     * @return new Score that is the average of this and other
     */
    public Score<T> incrementalAverage(Score<T> other) {
        if (other == null) {
            return this.copy();
        }
        assert (this.scores.length == other.scores.length);
        assert (this.otherStats.length == other.otherStats.length);

        double[] thisWeightedScores = ArrayUtil.scale(this.scores, (this.evals * 1.0) / (this.evals + other.evals));
        double[] otherWeightedScores = ArrayUtil.scale(other.scores, (other.evals * 1.0) / (this.evals + other.evals));
        double[] scoresAvg = ArrayUtil.zipAdd(thisWeightedScores, otherWeightedScores);

        double[] thisWeightedOtherStats = ArrayUtil.scale(this.otherStats, (this.evals * 1.0) / (this.evals + other.evals));
        double[] otherWeightedOtherStats = ArrayUtil.scale(other.otherStats, (other.evals * 1.0) / (this.evals + other.evals));
        double[] otherStatsAvg = ArrayUtil.zipAdd(thisWeightedOtherStats, otherWeightedOtherStats);

        Score<T> result = new Score<T>(individual, scoresAvg, behaviorVector, otherStatsAvg);
        result.evals = this.evals + other.evals;
        return result;
    }

    public Score<T> copy() {
        return new Score<T>(individual, Arrays.copyOf(scores, scores.length), behaviorVector, Arrays.copyOf(otherStats, otherStats.length));
    }

    public int numObjectives() {
        return scores.length;
    }

    public boolean isBetter(Score<T> other) {
        return scores[0] > other.scores[0];
    }

    public boolean isAtLeastAsGood(Score<T> other) {
        return scores[0] >= other.scores[0];
    }

    public boolean isWorse(Score<T> other) {
        return scores[0] < other.scores[0];
    }

    public void extraScore(double score) {
        double[] newScores = new double[scores.length + 1];
        System.arraycopy(scores, 0, newScores, 0, scores.length);
        newScores[scores.length] = score;
        scores = newScores;
    }
    
    /**
     * Delete the score from the last objective from the list of scores,
     * which also decreases the number of objectives
     */
    public void dropLastScore() {
        double[] newScores = new double[scores.length - 1];
        System.arraycopy(scores, 0, newScores, 0, newScores.length);
        scores = newScores;
    }

    @Override
    public String toString() {
        return (individual == null ? "NULL" : individual.getId()) + ":N=" + evals + ":" + Arrays.toString(scores) + (otherStats.length > 0 ? Arrays.toString(otherStats) : "");
    }

    public void giveBehaviorVector(ArrayList<Double> behaviorVector) {
        if (behaviorVector != null) {
            System.out.println("Behavior ArrayList: " + behaviorVector);
        }
        this.behaviorVector = behaviorVector;
    }

    public Score maxScores(Score other) {
        if (other == null) {
            return this.copy();
        }
        assert (this.scores.length == other.scores.length);
        assert (this.otherStats.length == other.otherStats.length);

        Score<T> result = new Score<T>(individual, ArrayUtil.zipMax(this.scores, other.scores), behaviorVector, ArrayUtil.zipMax(this.otherStats, other.otherStats));
        result.evals = this.evals + other.evals;
        return result;
    }
}
