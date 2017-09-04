package edu.southwestern.tasks.mspacman;

import edu.southwestern.networks.Network;
import edu.southwestern.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;

/**
 * Evolves several populations of subnetworks, and each time-step a
 * human-defined multitask scheme picks one to control the pacman agent.
 *
 * @author Jacob Schrum
 * @param <T> phenotype
 */
public class CooperativeMultitaskSchemeMsPacManTask<T extends Network>
		extends CooperativeNonHierarchicalMultiNetMsPacManTask<T> {

	public CooperativeMultitaskSchemeMsPacManTask() {
		this(null);
	}

	public CooperativeMultitaskSchemeMsPacManTask(MsPacManControllerInputOutputMediator[] mediators) {
		super(-1, false, "pacmanMultitaskScheme", mediators);
	}
}
