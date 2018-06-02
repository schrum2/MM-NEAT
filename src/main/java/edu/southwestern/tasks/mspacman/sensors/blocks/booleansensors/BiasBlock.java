package edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 * Supports popacman (TODO: test)
 * @author Jacob Schrum
 */
public class BiasBlock extends BooleanSensorBlock {

	@Override
	public String senseLabel() {
		return "Bias";
	}

	@Override
	public boolean predicate(GameFacade gf, int lastDirection) {
		return true;
	}

	// Don't need caching for simple computations
	@Override
	public int retrieveSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
		return incorporateSensors(inputs, in, gf, lastDirection);
	}
}
