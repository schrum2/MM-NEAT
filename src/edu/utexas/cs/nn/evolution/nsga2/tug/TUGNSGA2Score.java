package edu.utexas.cs.nn.evolution.nsga2.tug;

import edu.utexas.cs.nn.evolution.nsga2.NSGA2Score;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import java.util.Arrays;

/**
 * Same as NSGA2 score, but removes inactive objectives when comparing
 *
 * @author Jacob Schrum
 */
public class TUGNSGA2Score<T> extends NSGA2Score<T> {

    private final boolean[] active;

    public TUGNSGA2Score(Score<T> s, boolean[] active) {
        super(s.individual, s.scores, s.behaviorVector, s.otherStats);
        this.active = active;
    }

    @Override
    public String toString() {
        return "ALL:" + super.toString() + ":FILTERED BY: " + Arrays.toString(active) + " = " + Arrays.toString(filterByGoals(scores, active));
    }

    private static double[] filterByGoals(double[] scores, boolean[] active) {
        int activeObjectives = ArrayUtil.countOccurrences(true, active);
        double[] reducedObjectives = new double[activeObjectives];
        int index = 0;
        for (int i = 0; i < active.length; i++) {
            if (active[i]) {
                reducedObjectives[index++] = scores[i];
            }
        }
        //System.out.println("\t" + Arrays.toString(scores) + " reduced to " + Arrays.toString(reducedObjectives) + " based on " + Arrays.toString(active));
        return reducedObjectives;
    }

    @Override
    public boolean isBetter(Score<T> other) {
        NSGA2Score<T> lhs = new NSGA2Score<T>(individual, filterByGoals(scores, active), behaviorVector, otherStats);
        NSGA2Score<T> rhs = new NSGA2Score<T>(other.individual, filterByGoals(other.scores, active), other.behaviorVector, other.otherStats);
        return lhs.isBetter(rhs);
    }

    @Override
    public boolean isWorse(Score<T> other) {
        NSGA2Score<T> lhs = new NSGA2Score<T>(individual, filterByGoals(scores, active), behaviorVector, otherStats);
        NSGA2Score<T> rhs = new NSGA2Score<T>(other.individual, filterByGoals(other.scores, active), other.behaviorVector, other.otherStats);
        return lhs.isWorse(rhs);
    }
    
    @Override
    public boolean useObjective(int objective) {
        return active[objective];
    }
}
