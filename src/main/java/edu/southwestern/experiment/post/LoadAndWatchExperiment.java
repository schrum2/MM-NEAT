package edu.southwestern.experiment.post;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.nsga2.NSGA2Score;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.SinglePopulationTask;
import edu.southwestern.util.PopulationUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Load members of the last saved generation from an evolution experiment and
 * evaluate each member. Optionally, the population can be filtered down to the
 * Pareto front before evaluation, which is the most sensible option.
 *
 * @author Jacob Schrum
 * @param <T> phenotype
 */
public final class LoadAndWatchExperiment<T> implements Experiment {

	protected ArrayList<Genotype<T>> population;
	protected SinglePopulationTask<T> task;

	/**
	 * Load last saved population, and potentially filter it down to just the
	 * Pareto front.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		String lastSavedDir = Parameters.parameters.stringParameter("lastSavedDirectory");
		// Currently does not work with co-evolution. Other experiments handle these cases
		this.task = (SinglePopulationTask<T>) MMNEAT.task;
		if (lastSavedDir == null || lastSavedDir.equals("")) {
			System.out.println("Nothing to load");
			System.exit(1);
		} else {
			System.out.println("Loading: " + lastSavedDir);
			population = PopulationUtil.load(lastSavedDir);
			if (Parameters.parameters.booleanParameter("onlyWatchPareto")) {
				NSGA2Score<T>[] scores = null;
				try {
					scores = PopulationUtil.loadScores(Parameters.parameters.integerParameter("lastSavedGeneration"));
				} catch (FileNotFoundException ex) {
					ex.printStackTrace();
					System.exit(1);
				}
				PopulationUtil.pruneDownToParetoFront(population, scores);
			}
		}
	}

	/**
	 * Evaluate all members still in population
	 */
	@Override
	public void run() {
		System.out.println("Looking at results for " + task);
		task.evaluateAll(population);
	}

	/**
	 * Never called
	 */
	@Override
	public boolean shouldStop() {
		return true;
	}
}
