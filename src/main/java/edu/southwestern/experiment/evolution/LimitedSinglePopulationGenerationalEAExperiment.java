package edu.southwestern.experiment.evolution;

import edu.southwestern.evolution.SinglePopulationGenerationalEA;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.MMNEAT.MMNEAT;

/**
 * A generational experiment that stops when a specific number of generations
 * is exceeded.
 * 
 * @author Jacob Schrum
 * @param <T> phenotype
 */
public class LimitedSinglePopulationGenerationalEAExperiment<T> extends SinglePopulationGenerationalEAExperiment<T> {

	private int maxGenerations;

	/**
	 * Creates a default instance based on parameters loaded from the commandline
	 */
	@SuppressWarnings("unchecked")
	public LimitedSinglePopulationGenerationalEAExperiment() {
		this((SinglePopulationGenerationalEA<T>) MMNEAT.ea, MMNEAT.genotype,
				Parameters.parameters.integerParameter("maxGens"),
				Parameters.parameters.stringParameter("lastSavedDirectory"));
	}

	/**
	 * Creates experiment instance with specified parameters.
	 * @param ea evolutionary algorithm that works on a single population
	 * @param example template for the initial genotype of the population
	 * @param maxGenerations number of generations of evolution
	 * @param lastSavedDir last directory to which genotypes were saved, if saving occurred (null or "" otherwise)
	 */
	public LimitedSinglePopulationGenerationalEAExperiment(SinglePopulationGenerationalEA<T> ea, Genotype<T> example, int maxGenerations, String lastSavedDir) {
		super(ea, example, lastSavedDir);
		this.maxGenerations = maxGenerations;
	}

	/**
	 * Indicates that the experiment should stop once the maxGenerations have been reached
	 */
	@Override
	public boolean shouldStop() {
		return ea.currentGeneration() >= this.maxGenerations;
	}
}
