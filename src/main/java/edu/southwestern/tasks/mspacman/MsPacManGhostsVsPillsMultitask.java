package edu.southwestern.tasks.mspacman;

import edu.southwestern.networks.Network;

/**
 * One pacman eval consists of two separate evals: One in the regular game, and
 * one in the ghost eating scenario.
 *
 * @author Jacob Schrum
 * @param <T>
 */
public class MsPacManGhostsVsPillsMultitask<T extends Network> extends MsPacManIsolatedMultitask<T> {

	public void task1Prep() {
		// Do an eval in the Ghost Only version
		noPills = true;
		endAfterGhostEatingChances = false;
		noPowerPills = false;
	}

	public void task2Prep() {
		// Now do an eval in the Pill Only version
		noPills = false;
		endAfterGhostEatingChances = false;
		noPowerPills = true;
	}
}
