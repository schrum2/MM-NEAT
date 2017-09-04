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
public class RawTimeScore<T extends Network> extends MsPacManObjective<T> {

	public double fitness(Organism<T> individual) {
		return g.getTotalTime();
	}
}
