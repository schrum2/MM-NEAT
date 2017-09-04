package edu.southwestern.scores;

import java.util.Comparator;

/**
 * Comparator method that compares scores of agents
 *
 * @author Jacob Schrum
 * @param <T> phenotype
 */
public class ObjectiveComparator<T> implements Comparator<Score<T>> {

	private final int objectiveIndex;// index of scores to be compared

	/**
	 * Constructor for ObjectiveComparator.
	 * 
	 * @param index:
	 *            index used to determine scores to be compared
	 */
	public ObjectiveComparator(int index) {
		objectiveIndex = index;
	}

	/**
	 * Comparator for two objects that extend ObjectiveComparator
         * @param o1 first score
         * @param o2 second score
         * @return -1 (less), 0 (equal), or 1 (greater)
	 */
        @Override
	public int compare(Score<T> o1, Score<T> o2) {
		double diff = o1.scores[objectiveIndex] - o2.scores[objectiveIndex];
		return (int) Math.signum(diff);
	}
}
