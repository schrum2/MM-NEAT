/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.evolution.metaheuristics;

import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;

/**
 * Fitness that rewards networks for equally using a specific number of modes
 *
 * @author Jacob Schrum
 */
public class FavorXModesFitness extends AntiMaxModeUsageFitness {

    private final int preferredNumModes;

    public FavorXModesFitness(int target) {
        this.preferredNumModes = target;
    }

    @Override
    public double getScore(TWEANNGenotype g) {
        return -Math.abs(g.maxModeUsage() - (1.0 / preferredNumModes));
    }
}
