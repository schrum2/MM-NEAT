package edu.utexas.cs.nn.evolution.nsga2.tug;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.nsga2.CoevolutionNSGA2;
import edu.utexas.cs.nn.scores.Score;
import java.util.ArrayList;

/**
 * Defines TUGNSGA2 for coevolution
 * @author Jacob
 */
public class CoevolutionTUGNSGA2 extends CoevolutionNSGA2 {

	@SuppressWarnings("rawtypes")
	private final TUGNSGA2 tug;

	@SuppressWarnings("rawtypes")
	/**
	 * creates an instance of CoevolutionTUGNSGA2
	 */
	public CoevolutionTUGNSGA2() {
		this.tug = new TUGNSGA2(false);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	/**
	 * Selects the genotypes to be used in the next generation based off of the scores of the parents
	 * and the children and TUG
	 * Does not remove the achieved goals
	 * 
	 * @param popIndex, an int describing which is the current population
	 * @param toKeep, an integer indicating the number of parents to keep
	 * @param sourcePopulation, an arrayList of Scores from the source population
	 */
	public ArrayList<Genotype> selection(int popIndex, int toKeep, ArrayList<Score> sourcePopulation) {
		if (popIndex == 0) {
			// Use TUG
			ArrayList<Genotype> result = tug.selection(toKeep, sourcePopulation);
			tug.generation++;
			return result;
		} else {
			return super.selection(popIndex, toKeep, sourcePopulation);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	/**
	 * finishes up with logging and closes related io file content and logs
	 * @param population, an arrayList of Genotypes of the population
	 */
	public void close(ArrayList<ArrayList<Genotype>> populations) {
		super.close(populations);
		if (writeOutput) {
			tug.tugLog.close();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	/**
	 * Alter the source population according to the TUG goal and objective adjustment
	 * based on which goals have been achieved and which goals have not (the goals
	 * that have not been achieved will be targeted/used)
	 * Also log information from TUG
	 * 
	 * @param popIndex, the index of the current population
	 * @param parentScores, an arrayList of Scores of the parents
	 * @param childrenScores, an arrayList of Scores of the children
	 * @param mltype, an int indicating which type of muLambda is being used
	 */
	public ArrayList<Score> prepareSourcePopulation(int popIndex, ArrayList<Score> parentScores, ArrayList<Score> childrenScores, int mltype) {
		if (popIndex == 0) {
			// TUG log management
			double[][] stats = tug.performanceAndGoalTargets(parentScores);
			double[] averages = stats[0];
			double[] maxes = stats[1];
			tug.adjustGoalsAndObjectives(averages, maxes);
			tug.logOut(averages, maxes);
		}
		return super.prepareSourcePopulation(popIndex, parentScores, childrenScores, mltype);
	}
}
