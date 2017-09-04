package edu.southwestern.tasks.mspacman.sensors.blocks.hyperneat;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.agentcontroller.pacman.NNHyperNEATPacManController;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
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
			if(Parameters.parameters.booleanParameter("senseHyperNEATGhostPath")) {
				int[] trail = gf.getGhostPath(i, gf.getPacmanCurrentNodeIndex());
				for(int j = 0; j < trail.length; j++) {
					int node2 = trail[j];
					int x = gf.getNodeXCoord(node2);
					int y = gf.getNodeYCoord(node2);
					int inputOffset = NNHyperNEATPacManController.getOutputIndexFromNodeCoord(x, y);
					if(gf.isGhostEdible(i)) {
						inputs[startPoint + inputOffset] += 1.0*(trail.length - j) / trail.length;
					} else {
						inputs[startPoint + inputOffset] += -1.0*(trail.length - j) / trail.length;	
					}
				}
			}
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
