package edu.southwestern.tasks.mspacman.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;

/**
 * Return the ghost reward score, which is scaled to behave similarly to the
 * portion of the Ms. Pac-Man game score that comes from eating ghosts.
 * 
 * @author Jacob Schrum
 */
public class GhostRewardScore<T extends Network> extends MsPacManObjective<T> {

	/**
	 * Ghost reward is stored in the GameFacade. The Organism individual is not
	 * needed.
	 */
	public double fitness(Organism<T> individual) {
		return g.getGhostReward();
	}
}
