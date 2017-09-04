/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;

/**
 *
 * @author Jacob Schrum
 */
public class GhostsPerPowerPillScore<T extends Network> extends MsPacManObjective<T> {
	private final boolean punishUneatenPowerPills;

	public GhostsPerPowerPillScore(boolean punishUneatenPowerPills) {
		this.punishUneatenPowerPills = punishUneatenPowerPills;
	}

	public double fitness(Organism<T> individual) {
		return g.averageGhostsEatenPerPowerPill(punishUneatenPowerPills);
	}
}
