package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.hyperneat;

import edu.utexas.cs.nn.tasks.mspacman.MsPacManTask;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
/**
 * Abstract template class for substrate sensor block
 * for HyperNEAT in ms PacMan
 * @author gillespl
 *
 */
public abstract class FullScreenSubstrateSensorBlock extends MsPacManSensorBlock{

	@Override
	/**
	 * Returns number of sensors added
	 * @return num sensors added
	 */
	public int numberAdded() {
		return MsPacManTask.MS_PAC_MAN_SUBSTRATE_SIZE;
	}
	
	@Override
	/**
	 * incorporates labels to labels array
	 * @param labels array containing all sensor labels
	 * @param ending index in labels array
	 */
	public int incorporateLabels(String[] labels, int in) {
		for(int y = 0; y < MsPacManTask.MS_PAC_MAN_SUBSTRATE_HEIGHT; y++) {
			for(int x = 0; x < MsPacManTask.MS_PAC_MAN_SUBSTRATE_WIDTH; x++) {
				labels[in++] = "("+x+","+y+") " + locationLabel();
			}
		}
		return in;
	}

	/**
	 * Gets the string label associated with the x-y coordinates
	 * from sensor label
	 * @return String sensor label
	 */
	public abstract String locationLabel();
}
