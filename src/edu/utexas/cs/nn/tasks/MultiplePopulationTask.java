package edu.utexas.cs.nn.tasks;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.scores.Score;
import java.util.ArrayList;

/**
 * Annoying Programming Languages problem: I want to keep this interface general
 * by allowing for an arbitrary number of subpopulations, but I also want each
 * subpopulation to potentially hold genotypes of a different type. Because the
 * number of populations is unknown, they either all have to have the same type,
 * or not have a designated type at all.
 *
 * @author He_Deceives
 */
public interface MultiplePopulationTask extends Task {

	public ArrayList<ArrayList<Score>> evaluateAllPopulations(ArrayList<ArrayList<Genotype>> populations);

	public int numberOfPopulations();

	public int[] objectivesPerPopulation();

	public int[] otherStatsPerPopulation();
}
