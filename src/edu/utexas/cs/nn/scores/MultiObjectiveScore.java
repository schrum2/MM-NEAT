package edu.utexas.cs.nn.scores;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class MultiObjectiveScore<T> extends Score<T> {

    public static final double SMALL_DIFFERENCE = Double.MIN_VALUE;

    public MultiObjectiveScore(Genotype<T> individual, double[] scores, ArrayList<Double> behaviorVector) {
        this(individual, scores, behaviorVector, new double[0]);
    }

    public MultiObjectiveScore(Genotype<T> individual, double[] scores, ArrayList<Double> behaviorVector, double[] otherStats) {
        super(individual, scores, behaviorVector, otherStats);
    }

    public double objectiveScore(int index) {
        return scores[index];
    }

    @Override
    public boolean isBetter(Score<T> other) {
        int betterObjectives = 0;
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

    @Override
    public boolean isAtLeastAsGood(Score<T> other) {
        int betterObjectives = 0;
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

    @Override
    public boolean isWorse(Score<T> other) {
        int worseObjectives = 0;
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
        return true;
    }
}
