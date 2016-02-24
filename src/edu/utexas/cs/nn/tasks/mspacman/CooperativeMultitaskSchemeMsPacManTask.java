package edu.utexas.cs.nn.tasks.mspacman;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;

/**
 * Evolves several populations of subnetworks, and each time-step a
 * human-defined multitask scheme picks one to control the pacman agent.
 *
 * @author Jacob Schrum
 */
public class CooperativeMultitaskSchemeMsPacManTask<T extends Network> extends CooperativeNonHierarchicalMultiNetMsPacManTask<T> {

    public CooperativeMultitaskSchemeMsPacManTask() {
        this(null);
    }

    public CooperativeMultitaskSchemeMsPacManTask(MsPacManControllerInputOutputMediator[] mediators) {
        super(-1, false, "pacmanMultitaskScheme", mediators);
    }
}
