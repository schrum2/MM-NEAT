/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;

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
