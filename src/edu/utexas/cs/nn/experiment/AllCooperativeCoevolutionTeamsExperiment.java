package edu.utexas.cs.nn.experiment;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.nsga2.NSGA2Score;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.CooperativeTask;
import edu.utexas.cs.nn.util.CombinatoricUtilities;
import edu.utexas.cs.nn.util.PopulationUtil;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Load saved results from coevolution experiment and evaluate every possible
 * team combination to get their scores.
 *
 * @author Jacob Schrum
 */
public class AllCooperativeCoevolutionTeamsExperiment implements Experiment {

	private ArrayList<ArrayList<Genotype>> populations;
	private CooperativeTask task;

        @Override
	public void init() {
		task = (CooperativeTask) MMNEAT.task;
		String lastSavedDir = Parameters.parameters.stringParameter("lastSavedDirectory");
		int numPops = task.numberOfPopulations();
		populations = PopulationUtil.loadSubPops(lastSavedDir, numPops);
		if (Parameters.parameters.booleanParameter("onlyWatchPareto")) {
			NSGA2Score[] scores = null;
			int gen = Parameters.parameters.integerParameter("lastSavedGeneration");
			int topLayers = Parameters.parameters.integerParameter("layersToView");
			try {
				for (int i = 0; i < populations.size(); i++) {
					ArrayList<Genotype<Object>> tempPopulation = PopulationUtil.addListGenotypeType(populations.get(i));
					scores = PopulationUtil.loadSubPopScores(gen, i);
					// TODO: This method is removing ALL individuals in the temp population. This should not happen. What is the problem?
					PopulationUtil.pruneDownToTopParetoLayers(tempPopulation, scores, topLayers);
					populations.set(i, PopulationUtil.removeListGenotypeType(tempPopulation));
				}
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		}
	}

        @Override
	public void run() {
		ArrayList<Integer> lengths = new ArrayList<Integer>(populations.size());
		for (ArrayList<Genotype> pop : populations) {
			lengths.add(pop.size());
		}
		List<ArrayList<Integer>> combos = CombinatoricUtilities.getAllCombinations(lengths);
		List<ArrayList<Integer>> joinOrder = invert(combos);
		task.evaluateAllPopulations(populations, joinOrder);
	}

        @Override
	public boolean shouldStop() {
		// Will never be called
		return true;
	}

	/**
	 * Treats vector of vectors like a 2D matrix and inverts it into a new
	 * vector of vectors with columns and rows swapped.
	 *
	 * This method should probably be moved into a utility class, but it's not
	 * clear where at the moment.
	 *
         * @param <T>
	 * @param mat
	 *            vector of vectors
	 * @return vector of vectors representing inverse of mat
	 */
	public static <T> List<ArrayList<T>> invert(List<ArrayList<T>> mat) {
		int cols = mat.size();
		int rows = mat.get(0).size();
		ArrayList<ArrayList<T>> result = new ArrayList<ArrayList<T>>(rows);

		for (int i = 0; i < rows; i++) {
			result.add(new ArrayList<T>(cols));
			for (int j = 0; j < cols; j++) {
				result.get(i).add(mat.get(j).get(i));
			}
		}

		return result;
	}
}
