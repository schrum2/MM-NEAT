/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.blocks.time;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import java.util.Arrays;
import pacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public class EdibleTimesBlock extends MsPacManSensorBlock {

	protected static final int DANGEROUS_TIME = 5;
	private final boolean[] mask;
	private final int absence;
	private final int numSensors;

	@Override
	public boolean equals(MsPacManSensorBlock o) {
		if (o != null && o.getClass() == this.getClass()) {
			EdibleTimesBlock other = (EdibleTimesBlock) o;
			return Arrays.equals(this.mask, other.mask);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 17 * hash + Arrays.hashCode(this.mask);
		hash = 17 * hash + super.hashCode();
		return hash;
	}

	public EdibleTimesBlock() {
		this(new boolean[] { false, false, false, true });
	}

	public EdibleTimesBlock(boolean[] mask) {
		this.mask = mask;
		int total = 0;
		for (int i = 0; i < mask.length; i++) {
			if (mask[i]) {
				total++;
			}
		}
		numSensors = total;
		this.absence = Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0;
	}

	public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
		int[] times = gf.getGhostEdibleTimes();
		Arrays.sort(times); // smallest to highest
		// System.out.println(Arrays.toString(mask) + Arrays.toString(times) +
		// ":" + Constants.EDIBLE_TIME);
		for (int i = times.length - 1; i >= 0; i--) {
			if (mask[i]) {
				// inputs[in++] = times[i] == 0 ? absence : times[i] /
				// ((Constants.EDIBLE_TIME - DANGEROUS_TIME) * 1.0);
				inputs[in++] = times[i] == 0 ? absence : times[i] / (Constants.EDIBLE_TIME * 1.0);
			}
		}
		return in;
	}

	public int incorporateLabels(String[] labels, int in) {
		for (int i = 0; i < mask.length; i++) {
			if (mask[i]) {
				labels[in++] = i + " Highest Edible Time";
			}
		}
		return in;
	}

	public int numberAdded() {
		return numSensors;
	}

	@Override
	public void reset() {
	}

	@Override
	public void updateLastDir(int dir) {
	}
}
