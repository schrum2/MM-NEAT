/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.networks.Network;

/**
 *
 * @author Jacob Schrum
 */
public class SpecificGhostScore<T extends Network> extends MsPacManObjective<T> {

    private final int ghostIndex;

    public SpecificGhostScore(int ghostIndex) {
        this.ghostIndex = ghostIndex;
    }

    public double fitness(Organism<T> individual) {
        return g.getSpecificGhostEatenCount(ghostIndex);
    }
}
