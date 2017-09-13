package edu.southwestern.experiment.evolution;

import edu.southwestern.evolution.SteadyStateEA;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.parameters.Parameters;

public class SteadyStateExperiment<T> implements Experiment {

	private SteadyStateEA<T> ea;
	private int maxIterations;
	
	public SteadyStateExperiment(SteadyStateEA<T> ea, Genotype<T> example) {
		this.ea = ea;
		this.ea.initialize(example);
		// Overriding the meaning of maxGens to treat it like maxIterations
		maxIterations = Parameters.parameters.integerParameter("maxGens");
	}
	
	@Override
	public void init() {
		// Init of EA was called in constructor instead
	}

	@Override
	public void run() {
		while(!shouldStop()) {
			ea.newIndividual();
		}
	}

	@Override
	public boolean shouldStop() {
		return ea.currentIteration() >= maxIterations;
	}

}
