package edu.southwestern.breve2D.agent;

import edu.southwestern.breve2D.Breve2DGame;

public final class StationaryAgent implements AgentController {
	/**
	 * Default constructor
	 * 
	 * Because Breve2DAction input is 0, agent will remain stationary
	 */
	public Breve2DAction getAction(Breve2DGame game) {
		return new Breve2DAction(0, 0);
	}
	
	/**
	 * Resets players - currently empty
	 */
	public void reset() {
	}
}
