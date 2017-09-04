package edu.southwestern.tasks.mspacman.agentcontroller.pacman.actions;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 * A high-level action for Ms Pacman to follow.
 *
 * @author Jacob Schrum
 */
public interface MsPacManAction {

	public int getMoveAction(GameFacade gf);
}
