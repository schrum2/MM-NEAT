package edu.southwestern.tasks.mspacman.sensors.blocks.coords;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.raw.MirroredYPosBlock;

/**
 *
 * @author Jacob
 */
public class SpecificGhostYOffsetBlock extends MsPacManSensorBlock {
	private final int ghostIndex;

	public SpecificGhostYOffsetBlock(int ghostIndex) {
		this.ghostIndex = ghostIndex;
	}

	@Override
	public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
		int pacman = gf.getPacmanCurrentNodeIndex();
		int ghost = gf.getGhostCurrentNodeIndex(ghostIndex);
		int pY = gf.getNodeYCoord(pacman);
		int gY = gf.getNodeYCoord(ghost);
		inputs[in++] = (gY - pY) / (1.0 * MirroredYPosBlock.MAX_Y_COORD);
		return in;
	}

	@Override
	public int incorporateLabels(String[] labels, int in) {
		labels[in++] = "Y Offset to Ghost " + ghostIndex;
		return in;
	}

	@Override
	public int numberAdded() {
		return 1;
	}
}
