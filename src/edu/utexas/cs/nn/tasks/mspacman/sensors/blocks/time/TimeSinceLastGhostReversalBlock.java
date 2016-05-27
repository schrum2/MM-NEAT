/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.time;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 *
 * @author Jacob
 */
public class TimeSinceLastGhostReversalBlock extends MsPacManSensorBlock {

	@Override
	public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
		int timeSince = gf.timeSinceLastGlobalReversal();
		int maxTime = gf.getNextEdibleTime();
		// System.out.println("timeSince:"+timeSince+":maxTime:"+maxTime);
		inputs[in++] = timeSince == -1 ? 1.0 : Math.min(timeSince, maxTime) / (1.0 * maxTime);
		return in;
	}

	@Override
	public int incorporateLabels(String[] labels, int in) {
		labels[in++] = "Time Since Last Ghost Reversal";
		return in;
	}

	@Override
	public int numberAdded() {
		return 1;
	}
}
