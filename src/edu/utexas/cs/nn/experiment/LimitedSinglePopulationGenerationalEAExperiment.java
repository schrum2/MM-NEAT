package edu.utexas.cs.nn.experiment;

import edu.utexas.cs.nn.evolution.SinglePopulationGenerationalEA;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;

/**
 *
 * @author Jacob Schrum
 */
public class LimitedSinglePopulationGenerationalEAExperiment<T> extends SinglePopulationGenerationalEAExperiment<T> {

    private int maxGenerations;

    public LimitedSinglePopulationGenerationalEAExperiment() {
        this((SinglePopulationGenerationalEA<T>) MMNEAT.ea, MMNEAT.genotype, Parameters.parameters.integerParameter("maxGens"), Parameters.parameters.stringParameter("lastSavedDirectory"));
    }

    public LimitedSinglePopulationGenerationalEAExperiment(SinglePopulationGenerationalEA<T> ea, Genotype<T> example, int maxGenerations, String lastSavedDir) {
        super(ea, example, lastSavedDir);
        this.maxGenerations = maxGenerations;
    }

    public boolean shouldStop() {
        return ea.currentGeneration() >= this.maxGenerations;
    }
}
