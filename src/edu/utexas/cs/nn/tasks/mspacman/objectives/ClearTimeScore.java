package edu.utexas.cs.nn.tasks.mspacman.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.CommonConstants;

/**
 * Meant to be used when only evolving on a single level. Reward pacman for
 * clearing the level quickly, but get worst fitness if pacman dies.
 *
 * @author Jacob Schrum
 */
public class ClearTimeScore<T extends Network> extends MsPacManObjective<T> {

    /**
     * Level time seems more appropriate here, but it resets before the fitness
     * can be checked, so total time is used instead, which makes sense as long
     * as this fitness function is only used in single level evaluation
     * (maxLevel = 1)
     *
     * @param individual
     * @return
     */
    public double fitness(Organism<T> individual) {
        return g.getPacmanNumberOfLivesRemaining() > 0
                ? -g.getTotalTime() // -g.getCurrentLevelTime()
                : -CommonConstants.pacManLevelTimeLimit;
    }
}
