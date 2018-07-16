/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;
import oldpacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public class LairTimeParameter<T extends Network> extends MsPacManObjective<T> {

	public double fitness(Organism<T> individual) {
		return Constants.COMMON_LAIR_TIME;
	}
}
