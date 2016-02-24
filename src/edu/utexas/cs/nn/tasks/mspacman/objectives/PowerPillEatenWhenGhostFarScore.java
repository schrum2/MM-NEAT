/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.networks.Network;

/**
 * Punish eating power pill if there were ghosts that were already edible or
 * were in the lair.
 *
 * @author Jacob Schrum
 */
public class PowerPillEatenWhenGhostFarScore<T extends Network> extends MsPacManObjective<T> {

    public double fitness(Organism<T> individual) {
        return -g.getPowerPillsEatenWhenGhostFar();
    }
}
