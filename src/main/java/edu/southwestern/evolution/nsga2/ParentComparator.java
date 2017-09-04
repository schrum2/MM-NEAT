package edu.southwestern.evolution.nsga2;

import edu.southwestern.scores.Better;

/**
 *
 * @author Jacob Schrum
 */
public class ParentComparator<T> extends CrowdingDistanceComparator<T>implements Better<NSGA2Score<T>> {

	@Override
	public int compare(NSGA2Score<T> o1, NSGA2Score<T> o2) {
		return (o1.getRank() == o2.getRank()) ? super.compare(o1, o2) : (o2.getRank() - o1.getRank());
	}

	public NSGA2Score<T> better(NSGA2Score<T> o1, NSGA2Score<T> o2) {
		NSGA2Score<T> winner = (compare(o1, o2) < 0) ? o2 : o1;
		return winner;
	}
}
