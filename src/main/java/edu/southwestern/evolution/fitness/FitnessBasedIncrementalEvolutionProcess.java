/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.evolution.fitness;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.util.stats.Statistic;
import java.util.ArrayList;

/**
 * This is a process that tracks performance in a particular objective (or any
 * score really) and modifies some aspect of the domain as the objective
 * achieves a particular goal. The domain should become more challenging with
 * each such update.
 *
 * The general approach has some similarities to TUG.
 *
 * @author Jacob
 */
public abstract class FitnessBasedIncrementalEvolutionProcess<T> {

	private final double minRWA;
	private double goal;
	private double recencyWeightedAverage;
	private double alpha;
	private FitnessBasedIncrementalEvolutionProcessLog stateHistory; // needed
																		// for
																		// resumes
	private Statistic performanceStat; // e.g. Average
	private final boolean writeOutput;

	public FitnessBasedIncrementalEvolutionProcess(double minRWA, double goal, String name) {
		this.minRWA = minRWA;
		this.goal = goal;
		this.writeOutput = Parameters.parameters.booleanParameter("io");
		if (writeOutput) {
			this.stateHistory = new FitnessBasedIncrementalEvolutionProcessLog(name, this);
		}
	}

	public void updateRecencyWeightedAverage(ArrayList<Score<T>> parentScores, int generation) {
		double[] scores = new double[parentScores.size()];
		for (int i = 0; i < parentScores.size(); i++) {
			scores[i] = extractScore(parentScores.get(i));
		}
		double performance = performanceStat.stat(scores);
		recencyWeightedAverage += alpha * (performance - recencyWeightedAverage);
		if (recencyWeightedAverage > goal && performance > goal) { // second
																	// check
																	// unnecessary?
			increaseDifficulty();
			recencyWeightedAverage = minRWA;
		}
		if (writeOutput) {
			ArrayList<Double> logData = new ArrayList<Double>();
			logData.add(performance);
			logData.add(goal);
			logData.add(recencyWeightedAverage);
			stateHistory.log(logData, generation);
		}
	}

	public void loadState(double performance, double goal, double rwa) {
		this.goal = goal;
		this.recencyWeightedAverage = rwa;
	}

	/**
	 * Given scores of organism, extract the one score that matters to
	 * incremental evolution.
	 *
	 * @param s
	 *            Score instance containing all scores associated with
	 *            organism's performance.
	 * @return
	 */
	public abstract double extractScore(Score<T> s);

	/**
	 * Make the task harder somehow
	 */
	public abstract void increaseDifficulty();
}
