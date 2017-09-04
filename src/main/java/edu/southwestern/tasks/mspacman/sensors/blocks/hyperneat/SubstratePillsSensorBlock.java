package edu.southwestern.tasks.mspacman.sensors.blocks.hyperneat;

import edu.southwestern.tasks.mspacman.agentcontroller.pacman.NNHyperNEATPacManController;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
/**
 * Gets sensor block for pill substrate in hyperNEAT msPacMan task
 * @author Lauren Gillespie
 *
 */
public class SubstratePillsSensorBlock extends FullScreenSubstrateSensorBlock {

	@Override
	/**
	 * adds sensor inputs from this block of sensor
	 * @param inputs inputs array
	 * @param startPoint starting point in inputs array for this sensor block
	 * @param lastDirection ms PacMan's last direction
	 * @return last used index in inputs array from this sensor block
	 */
	public int incorporateSensors(double[] inputs, int startPoint, GameFacade gf, int lastDirection) {
		int[] pills = gf.getActivePillsIndices();
		for(Integer node: pills) {
			int x = gf.getNodeXCoord(node);
			int y = gf.getNodeYCoord(node);
			int inputOffset = NNHyperNEATPacManController.getOutputIndexFromNodeCoord(x, y);
			inputs[startPoint + inputOffset] = 1;
		}
		return startPoint + numberAdded();
	}

	@Override
	/**
	 * Gets the string label associated with the x-y coordinates
	 * from sensor label
	 * @return String sensor label
	 */
	public String locationLabel() {
		return "Pill";
	}
}
