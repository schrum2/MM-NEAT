package edu.southwestern.evolution;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.log.FitnessLog;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.SinglePopulationTask;
import edu.southwestern.tasks.Task;
import java.util.ArrayList;

/**
 * Not really an Evolutionary Algorithm.
 * 
 * This EA is used to replay the evaluation of a previously saved population,
 * which is why several methods are unsupported.
 * 
 * @author Jacob Schrum
 * @param <T>
 *            Type of entity being evolved
 */
public class ReplayEA<T> implements SinglePopulationGenerationalEA<T> {

	public SinglePopulationTask<T> task;
	public int generation;
	protected FitnessLog<T> parentLog;

	/**
	 * Replay evaluation in a given task starting from specified generation
	 * 
	 * @param task
	 *            Task to replay
	 * @param gen
	 *            Generation to start at
	 */
	public ReplayEA(SinglePopulationTask<T> task, int gen) {
		this.task = task;
		this.generation = gen;
		parentLog = new FitnessLog<T>("parents");
	}

	// Simple getter method
	@Override
	public Task getTask() {
		return task;
	}

	// Simple getter method
	@Override
	public int currentGeneration() {
		return generation;
	}

	/**
	 * Just evaluates. Doesn't actually get next generation, which is loaded
	 * from disk within the PostEvolutionEvaluationExperiment, which is the only
	 * experiment that should really use this EA.
	 * 
	 * @param population
	 *            Population to evaluate
	 * @return Always null, because no evolution is actually occurring
	 */
	@Override
	public ArrayList<Genotype<T>> getNextGeneration(ArrayList<Genotype<T>> population) {
		ArrayList<Score<T>> parentScores = task.evaluateAll(population);
		parentLog.log(parentScores, generation);
		generation++;
		return null;
	}

	@Override
	public void close(ArrayList<Genotype<T>> population) {
		this.parentLog.close();
	}

	// Should never be called
	@Override
	public ArrayList<Genotype<T>> initialPopulation(Genotype<T> example) {
		throw new UnsupportedOperationException("Not supported for ReplayEA.");
	}

	// Should never be called
	@Override
	public int evaluationsPerGeneration() {
		throw new UnsupportedOperationException("Not supported for ReplayEA.");
	}
}
