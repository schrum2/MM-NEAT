/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.experiment;

import edu.utexas.cs.nn.evolution.MultiplePopulationGenerationalEA;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;

/**
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

    public boolean shouldStop() {
        return ea.currentGeneration() >= this.maxGenerations;
    }
}
