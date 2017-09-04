package edu.southwestern.tasks.mspacman.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;
import pacman.game.Game;

/**
 * Sloppy way of tracking the size of the path cache used by ms pacman.
 *
 * @author Jacob Schrum
 */
public class PathCacheSize<T extends Network> extends MsPacManObjective<T> {

	public double fitness(Organism<T> individual) {
		return Game.amountCachedPathData();
	}
}
