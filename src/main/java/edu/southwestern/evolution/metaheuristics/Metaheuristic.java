package edu.southwestern.evolution.metaheuristics;

import edu.southwestern.scores.Score;

/**
 * Any fitness function that is completely independent of the domain itself.
 * Properties of the genotypes/phenotypes themselves can be measured to create a
 * fitness function. These measurements are used to modify an existing Score
 * result.
 *
 * @author Jacob Schrum
 * @param <T>
 *            Type of phenotype being evolved (part of Score)
 */
public interface Metaheuristic<T> {

	/**
	 * Modify score instance by adding additional fitness score
	 * 
	 * @param s
	 *            Score instance to modify
	 */
	public void augmentScore(Score<T> s);

	/**
	 * Minimum score in this meta-objective
	 * 
	 * @return minimum score
	 */
	public double minScore();

	/**
	 * If used in TUG, what is the initial numeric goal
	 * 
	 * @return Initial TUG goal
	 */
	public double startingTUGGoal();
}
