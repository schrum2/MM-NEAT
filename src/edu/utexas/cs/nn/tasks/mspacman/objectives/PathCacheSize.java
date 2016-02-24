package edu.utexas.cs.nn.tasks.mspacman.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.networks.Network;
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
