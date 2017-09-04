package edu.southwestern.tasks.breve2D;

import edu.southwestern.breve2D.dynamics.Breve2DDynamics;
import edu.southwestern.networks.ModeSelector;

/**
 * Works for any MultitaskDynamics, but the correct number of modes still needs
 * to be specified by the multitaskModes parameter.
 *
 * @author Jacob Schrum
 */
public class BreveModeSelector implements ModeSelector {

	private final Breve2DDynamics dynamics;

	public BreveModeSelector(Breve2DDynamics dynamics) {
		this.dynamics = dynamics;
	}

        @Override
	public int mode() {
		return dynamics.task;
	}

        @Override
	public int numModes() {
		return dynamics.numTasks();
	}

        @Override
	public void reset() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
