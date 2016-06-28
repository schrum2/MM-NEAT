package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.hyperneat;

import edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.NNHyperNEATPacManController;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
/**
 * Gets sensor block for pacman substrate in hyperNEAT msPacMan task
 * @author Lauren Gillespie
 *
 */
public class SubstratePacManSensorBlock  extends FullScreenSubstrateSensorBlock{

	@Override
	/**
	 * Gets the string label associated with the x-y coordinates
	 * from sensor label
	 * @return String sensor label
	 */
	public String locationLabel() {
		return "Ms_PacMan";
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
		int pacManIndex = gf.getPacmanCurrentNodeIndex();
			int x = gf.getNodeXCoord(pacManIndex);
			int y = gf.getNodeYCoord(pacManIndex);
			inputs[startPoint + NNHyperNEATPacManController.getOutputIndexFromNodeCoord(x, y)] = 1;
		return startPoint + numberAdded();
	}

}
