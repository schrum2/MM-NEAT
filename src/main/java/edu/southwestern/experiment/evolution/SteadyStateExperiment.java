package edu.southwestern.experiment.evolution;

import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.SteadyStateEA;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.parameters.Parameters;

public class SteadyStateExperiment<T> implements Experiment {

	private SteadyStateEA<T> ea;
	private int maxIterations;
	private boolean cleanArchetype;

	@SuppressWarnings("unchecked")
	public SteadyStateExperiment() {
		this((SteadyStateEA<T>) MMNEAT.ea, MMNEAT.genotype);
	}
	
	public SteadyStateExperiment(SteadyStateEA<T> ea, Genotype<T> example) {
		this.ea = ea;
		this.ea.initialize(example);
		// Overriding the meaning of maxGens to treat it like maxIterations
		maxIterations = Parameters.parameters.integerParameter("maxGens");
		this.cleanArchetype = MMNEAT.genotype instanceof TWEANNGenotype;
	}
	
	@Override
	public void init() {
		// Init of EA was called in constructor instead
	}

	@Override
	public void run() {
		while(!shouldStop()) { // Until done
			ea.newIndividual(); // Make new individuals
			Parameters.parameters.saveParameters(); // Save the parameters and the archetype
			if(ea.populationChanged()) { // In steady state, not every individual is added to the population
				EvolutionaryHistory.saveArchetype(0);
			}
			if(cleanArchetype) { // Periodically clean extinct genes from the archetype
				ArrayList<Genotype<T>> pop = ea.getPopulation();
				ArrayList<TWEANNGenotype> tweannPop = new ArrayList<TWEANNGenotype>(pop.size());
				for(Genotype<T> g : pop) tweannPop.add((TWEANNGenotype) g);
				EvolutionaryHistory.cleanArchetype(0, tweannPop, ea.currentIteration());
			}
		}
		ea.finalCleanup();
	}

	@Override
	public boolean shouldStop() {
		return ea.currentIteration() >= maxIterations;
	}

}
