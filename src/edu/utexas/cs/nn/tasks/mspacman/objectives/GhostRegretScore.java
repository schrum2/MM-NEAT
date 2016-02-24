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
public class GhostRegretScore<T extends Network> extends MsPacManObjective<T> {

    public double fitness(Organism<T> individual) {
        return -g.getGhostRegret();
    }
}
