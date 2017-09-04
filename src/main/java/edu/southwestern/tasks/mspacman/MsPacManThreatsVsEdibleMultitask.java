package edu.southwestern.tasks.mspacman;

import edu.southwestern.networks.Network;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.Pair;

/**
 * One pacman eval consists of two separate evals: One in the regular game, and
 * one in the ghost eating scenario.
 *
 * @author Jacob Schrum
 * @param <T>
 */
public class MsPacManThreatsVsEdibleMultitask<T extends Network> extends MsPacManTask<T> {

	public MsPacManThreatsVsEdibleMultitask() {
		Parameters.parameters.setBoolean("infiniteEdibleTime", true);
		CommonConstants.infiniteEdibleTime = true;
		Parameters.parameters.setBoolean("imprisonedWhileEdible", true);
		CommonConstants.imprisonedWhileEdible = true;
	}

	public void task1Prep() {
		// Do an eval with only threats and a time limit
		noPills = true;
		noPowerPills = true;
		endOnlyOnTimeLimit = true;
		exitLairEdible = false;
		randomLairExit = false;
		simultaneousLairExit = false;
	}

	public void task2Prep() {
		// Now do an eval with only edible ghosts and a time limit
		noPills = true;
		noPowerPills = true;
		endOnlyOnTimeLimit = true;
		exitLairEdible = true;
		randomLairExit = true;
		simultaneousLairExit = true;
		// Edible task does not need to be as long as pill task
		CommonConstants.pacManLevelTimeLimit = Parameters.parameters.integerParameter("edibleTaskTimeLimit");
	}

	public void task2Post(Pair<double[], double[]> task2Results) {
		// Restore eval time for next pill task eval
		CommonConstants.pacManLevelTimeLimit = Parameters.parameters.integerParameter("pacManLevelTimeLimit");
		if (Parameters.parameters.booleanParameter("rawTimeScore")) {
			// Need to subtract time alive in edible task, since it is always
			// the max
			task2Results.t1[rawTimeScoreIndex] = 0;
		}
	}
}
