package edu.southwestern.tasks.mspacman;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.ensemble.MsPacManEnsembleArbitrator;
import edu.southwestern.util.ClassCreation;

/**
 * @author Jacob Schrum
 */
public class CooperativeGhostMonitorEnsembleMsPacManTask<T extends Network>
		extends CooperativeNonHierarchicalMultiNetMsPacManTask<T> {

	public CooperativeGhostMonitorEnsembleMsPacManTask() {
		super(CommonConstants.numActiveGhosts, true, "pacmanFitnessModeMap", true, null);
		try {
			MMNEAT.ensembleArbitrator = (MsPacManEnsembleArbitrator) ClassCreation.createObject("ensembleArbitrator");
		} catch (NoSuchMethodException ex) {
			System.out.println("Could not create ensemble arbitrator");
			System.exit(1);
		}
	}
}
