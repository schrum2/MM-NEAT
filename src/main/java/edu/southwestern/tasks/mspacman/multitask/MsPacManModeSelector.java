package edu.southwestern.tasks.mspacman.multitask;

import edu.southwestern.networks.ModeSelector;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.objectives.fitnessassignment.FitnessToModeMap;

/**
 * defines all of the Mode Selectors for ms PacMan
 * 
 * @author Jacob Schrum
 */
public abstract class MsPacManModeSelector implements ModeSelector, FitnessToModeMap {

	protected GameFacade gs;

	/**
	 * returns this instance of the gameFacade
	 * 
	 * @param gs,
	 *            the gameFacade
	 */
	public void giveGame(GameFacade gs) {
		this.gs = gs;
	}

	/**
	 * clears whatever components of the game/controller/mode are involved.
	 * Empty by default, so must define whatever will be cleared/reset
	 */
	@Override
	public void reset() {
		// Default is nothing
	}
}
