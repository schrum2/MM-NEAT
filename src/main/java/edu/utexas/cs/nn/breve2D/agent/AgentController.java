package edu.utexas.cs.nn.breve2D.agent;

import edu.utexas.cs.nn.breve2D.Breve2DGame;

/**
 *
 * @author Jacob Schrum
 */
public interface AgentController {

	/**
	 * Updates the Agent's actions throughout the course of a game based on the Agent's behavior
	 * 
	 * @param game A specific Breve2DGame instance
	 * @return Breve2DAction representing how the Agent should move
	 */
	public Breve2DAction getAction(Breve2DGame game);

	/**
	 * Resets the actions for the Agent
	 */
	public void reset();
}
