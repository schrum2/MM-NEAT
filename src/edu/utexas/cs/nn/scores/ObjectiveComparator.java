package edu.utexas.cs.nn.scores;

import java.util.Comparator;

/**
 *
 * @author Jacob Schrum
 */
public class ObjectiveComparator implements Comparator<Score> {

    private int objectiveIndex;

    public ObjectiveComparator(int index) {
        objectiveIndex = index;
    }

    public int compare(Score o1, Score o2) {
        double diff = o1.scores[objectiveIndex] - o2.scores[objectiveIndex];
        return (int) Math.signum(diff);
    }
}
