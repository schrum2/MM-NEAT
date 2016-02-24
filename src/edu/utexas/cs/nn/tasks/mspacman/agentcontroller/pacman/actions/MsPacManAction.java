package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.actions;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 * A high-level action for Ms Pacman to follow.
 *
 * @author Jacob Schrum
 */
public interface MsPacManAction {

    public int getMoveAction(GameFacade gf);
}
