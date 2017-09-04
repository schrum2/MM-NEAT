package edu.southwestern.breve2D.agent;

/**
 * Like the regular predator-prey enemy, but knows to reset all sub-controllers
 * when reset method is called.
 * 
 * @author Jacob Schrum
 */
public class InterleavedPredatorPreyEnemy extends PredatorPreyEnemy {
	
	/**
	 * Loops through all players and resets them.
	 */
	@Override
	public void reset() {
		// Each agent was used in eval, so each needs to be reset
		for (AgentController c : players) {
			c.reset();
		}
		// Always go back to first controller, no matter which task current eval
		// ended in
		task = 0;
	}
}
