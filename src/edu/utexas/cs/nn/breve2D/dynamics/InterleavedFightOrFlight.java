package edu.utexas.cs.nn.breve2D.dynamics;

import edu.utexas.cs.nn.parameters.Parameters;

/**
 *
 * @author Jacob Schrum
 */
public class InterleavedFightOrFlight extends FightOrFlight {

	private final int switchTime;

	public InterleavedFightOrFlight() {
		super();
		int timeLimit = Parameters.parameters.integerParameter("breve2DTimeLimit");
		switchTime = timeLimit / 2;
	}

	/**
	 * There are two interleaved tasks, but they make one isolated task, and
	 * therefore one eval.
	 * 
	 * @return Always 1
	 */
	@Override
	public int numIsolatedTasks() {
		return 1;
	}

	@Override
	public boolean midGameTaskSwitch(int timeStep) {
		return timeStep == switchTime;
	}

	@Override
	public void reset() {
		// Each task was used in eval, so each needs to be reset
		for (Breve2DDynamics d : tasks) {
			d.reset();
		}
		// Always go back to first task, no matter which task current eval ended
		// in
		task = 0;
	}
}
