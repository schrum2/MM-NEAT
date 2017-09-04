package edu.southwestern.tasks.mspacman.sensors.blocks.hyperneat;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.agentcontroller.pacman.NNHyperNEATPacManController;
import edu.southwestern.tasks.mspacman.facades.GameFacade;

public class SubstrateThreatSensorBlock extends FullScreenSubstrateSensorBlock{


	@Override
	/**
	 * Gets the string label associated with the x-y coordinates
	 * from sensor label
	 * @return String sensor label
	 */
	public String locationLabel() {
		return "Threat";
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
		for(Integer node: gf.getThreatGhostLocations()) {
			if(Parameters.parameters.booleanParameter("senseHyperNEATGhostPath")) {
				// index 0 gets the first ghost at that location. There could be a problem if there are two
				// ghosts going in opposite directions, but this should be rare enough to not be worth
				// worrying about.
				int[] trail = gf.getGhostPath(gf.getGhostIndexOfGhostAt(node)[0], gf.getPacmanCurrentNodeIndex());
				for(int j = 0; j < trail.length; j++) {
					int node2 = trail[j];
					int x = gf.getNodeXCoord(node2);
					int y = gf.getNodeYCoord(node2);
					int inputOffset = NNHyperNEATPacManController.getOutputIndexFromNodeCoord(x, y);
					inputs[startPoint + inputOffset] += -1.0; //*(trail.length - j) / trail.length;
				}
			}
			int x = gf.getNodeXCoord(node);
			int y = gf.getNodeYCoord(node);
			int inputOffset = NNHyperNEATPacManController.getOutputIndexFromNodeCoord(x, y);
			inputs[startPoint + inputOffset] = -1;
		}
		return startPoint + numberAdded();
	}

}
