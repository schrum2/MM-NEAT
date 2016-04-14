package edu.utexas.cs.nn.evolution.metaheuristics;

import edu.utexas.cs.nn.scores.Score;

/**
 * Any fitness function that is completely independent of the domain itself.
 * Properties of the genotypes/phenotypes themselves can be measured to
 * create a fitness function. These measurements are used to modify an
 * existing Score result.
 *
 * @author Jacob Schrum
 */
public interface Metaheuristic {

    @SuppressWarnings("rawtypes")
	public void augmentScore(Score s);

    public double minScore();

    public double startingTUGGoal();
}
