package edu.utexas.cs.nn.evolution.nsga2;

import edu.utexas.cs.nn.scores.Better;

/**
 * Sorts based on which solution dominates another in a
 * Pareto sense.
 *
 * @author Jacob Schrum
 */
public class Domination implements Better<NSGA2Score> {

    public NSGA2Score better(NSGA2Score e1, NSGA2Score e2) {
        return e1.isBetter(e2) ? e1 : e2;
    }
}
