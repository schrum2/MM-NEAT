package edu.southwestern.evolution.nsga2;

import edu.southwestern.scores.Better;

/**
 * Sorts based on which solution dominates another in a Pareto sense.
 *
 * @author Jacob Schrum
 */
public class Domination<T> implements Better<NSGA2Score<T>> {

	public NSGA2Score<T> better(NSGA2Score<T> e1, NSGA2Score<T> e2) {
		return e1.isBetter(e2) ? e1 : e2;
	}
}
