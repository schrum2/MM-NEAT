package edu.utexas.cs.nn.tasks.mspacman.multitask;

import edu.utexas.cs.nn.networks.ModeSelector;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.objectives.fitnessassignment.FitnessToModeMap;

/**
 * @author Jacob Schrum
 */
public abstract class MsPacManModeSelector implements ModeSelector, FitnessToModeMap {

    protected GameFacade gs;

    public void giveGame(GameFacade gs) {
        this.gs = gs;
    }

    public void reset() {
        // Default is nothing
    }
}
