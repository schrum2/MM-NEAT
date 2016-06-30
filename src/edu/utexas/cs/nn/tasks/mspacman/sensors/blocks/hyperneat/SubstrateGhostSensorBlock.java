package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.hyperneat;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.NNHyperNEATPacManController;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
/**
 * Gets sensor block for ghost substrate in hyperNEAT msPacMan task
 * @author Lauren Gillespie
 *
 */
public class SubstrateGhostSensorBlock  extends FullScreenSubstrateSensorBlock{

	@Override
	/**
	 * Gets the string label associated with the x-y coordinates
	 * from sensor label
	 * @return String sensor label
	 */
	public String locationLabel() {
		return "Ghost";
	}

	@Override
	/**
	 * adds sensor inputs from this block of sensor
	 * @param inputs inputs array
	 * @param startPoint starting point in inputs array for this sensor block
	 * @param lastDirection ms PacMan's last direction
	 * @return last used index in inputs array from this sensor block
	 */
	public int incorporateSensors(double[] inputs, int startPoint, GameFacade gf, int lastDirection) {
		for(int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			int node = gf.getGhostCurrentNodeIndex(i);
			int x = gf.getNodeXCoord(node);
			int y = gf.getNodeYCoord(node);
			int inputOffset = NNHyperNEATPacManController.getOutputIndexFromNodeCoord(x, y);
			if(gf.isGhostEdible(i)) {
				inputs[startPoint + inputOffset] = 1;
			} else {
				inputs[startPoint + inputOffset] = -1;
			}
		}
		return startPoint + numberAdded();
	}

}
