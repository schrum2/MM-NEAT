package edu.southwestern.experiment.evolution;

import edu.southwestern.evolution.MultiplePopulationGenerationalEA;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.MMNEAT.MMNEAT;

/**
 * A multi-population experiment that stops when a certain number of generations is exceeded.
 * 
 * @author Jacob Schrum
 */
public class LimitedMultiplePopulationGenerationalEAExperiment extends MultiplePopulationGenerationalEAExperiment {

	private int maxGenerations;

	public LimitedMultiplePopulationGenerationalEAExperiment() {
		this((MultiplePopulationGenerationalEA) MMNEAT.ea, Parameters.parameters.integerParameter("maxGens"));
	}

	public LimitedMultiplePopulationGenerationalEAExperiment(MultiplePopulationGenerationalEA ea, int maxGenerations) {
		super(ea);
		this.maxGenerations = maxGenerations;
	}

        @Override
	public boolean shouldStop() {
		return ea.currentGeneration() >= this.maxGenerations;
	}
}
