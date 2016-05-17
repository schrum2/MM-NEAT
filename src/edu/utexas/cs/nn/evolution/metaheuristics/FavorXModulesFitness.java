package edu.utexas.cs.nn.evolution.metaheuristics;

import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;

/**
 * Fitness that rewards networks for equally using a specific number of modes
 *
 * @author Jacob Schrum
 */
public class FavorXModulesFitness extends AntiMaxModuleUsageFitness {

    private final int preferredNumModes;

    public FavorXModulesFitness(int target) {
        this.preferredNumModes = target;
    }

    @Override
    public double getScore(TWEANNGenotype g) {
        return -Math.abs(g.maxModuleUsage() - (1.0 / preferredNumModes));
    }
}
