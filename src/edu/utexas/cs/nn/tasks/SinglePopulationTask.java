package edu.utexas.cs.nn.tasks;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.scores.Score;
import java.util.ArrayList;

/**
 *
 * @author He_Deceives
 */
public interface SinglePopulationTask<T> extends Task {
	/**
	 * a method that obtains a list of score evaluations of all of the genotypes of the population
	 * @param population
	 * @return list of scores
	 */
    public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population);
}
