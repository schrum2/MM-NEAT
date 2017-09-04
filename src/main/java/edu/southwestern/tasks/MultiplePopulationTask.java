package edu.southwestern.tasks;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.scores.Score;
import java.util.ArrayList;

/**
 * Any task that requires members from multiple separate populations.
 * The task can be cooperative, competitive, or both.
 * 
 * Annoying Programming Languages problem: I want to keep this interface general
 * by allowing for an arbitrary number of subpopulations, but I also want each
 * subpopulation to potentially hold genotypes of a different type. Because the
 * number of populations is unknown, they either all have to have the same type,
 * or not have a designated type at all.
 *
 * @author Jacob Schrum
 */
public interface MultiplePopulationTask extends Task {

        /**
         * Take a list of populations and evaluate every member of each 
         * population.
         * @param populations list of lists of genotypes. 
         *        Each sub-list is one population.
         * @return List of lists of scores for each genotype in each population.
         */
	@SuppressWarnings("rawtypes")
	public ArrayList<ArrayList<Score>> evaluateAllPopulations(ArrayList<ArrayList<Genotype>> populations);

        /**
         * Number of different evolving populations
         * @return number of populations
         */
	public int numberOfPopulations();

        /**
         * This should replace numObjectives() from Task in most cases.
         * It is somewhat problematic that both are present. This indicates
         * how many objectives each individual population has.
         * @return array of the objectives for each population
         */
	public int[] objectivesPerPopulation();

        /**
         * Number of other scores tracked for each population.
         * Any scores can be tracked, though for some of the
         * post processing to work, a convenient convention is to
         * have all relevant "other" scores associated with the
         * first population and have all others be empty/0.
         * 
         * @return number of other scores for each population.
         */
	public int[] otherStatsPerPopulation();
}
