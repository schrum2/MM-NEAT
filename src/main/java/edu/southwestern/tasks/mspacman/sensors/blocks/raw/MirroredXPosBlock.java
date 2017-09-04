package edu.southwestern.tasks.mspacman.sensors.blocks.raw;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 *
 * @author Jacob Schrum
 */
public class MirroredXPosBlock extends MsPacManSensorBlock {

	public static final int MAX_X_COORD = 108;

	public int incorporateSensors(double[] inputs, int startPoint, GameFacade gf, int lastDirection) {
		inputs[startPoint++] = (2.0 * gf.getNodeXCoord(gf.getPacmanCurrentNodeIndex()) / MAX_X_COORD) - 1;
		return startPoint;
	}

	public int incorporateLabels(String[] labels, int startPoint) {
		labels[startPoint++] = "Mirrored X Coord";
		return startPoint;
	}

	public int numberAdded() {
		return 1;
	}
}
