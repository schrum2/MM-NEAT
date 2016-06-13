package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import pacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public class GhostEatingRewardBlock extends MsPacManSensorBlock {

        @Override
	public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
		inputs[in++] = gf.anyIsEdible() ? (gf.getGhostCurrentEdibleScore() / Constants.GHOST_EAT_SCORE) / 8.0 : 0;
		return in;
	}

        @Override
	public int incorporateLabels(String[] labels, int in) {
		labels[in++] = "Next Eaten Ghost Value";
		return in;
	}

        @Override
	public int numberAdded() {
		return 1;
	}
}
