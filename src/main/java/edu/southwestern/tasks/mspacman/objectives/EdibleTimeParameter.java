package edu.southwestern.tasks.mspacman.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;
import oldpacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public class EdibleTimeParameter<T extends Network> extends MsPacManObjective<T> {

	public double fitness(Organism<T> individual) {
		return Constants.EDIBLE_TIME;
	}
}