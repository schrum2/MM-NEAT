package edu.southwestern.tasks.mspacman;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.ensemble.MsPacManEnsembleArbitrator;
import edu.southwestern.util.ClassCreation;

/**
 * Several sub-network populations are evolved and on each time step, some
 * ensemble arbitrator method picks an action based on the outputs of all
 * subnetworks.
 *
 * @author Jacob Schrum
 */
public class CooperativeEnsembleMsPacManTask<T extends Network>
		extends CooperativeNonHierarchicalMultiNetMsPacManTask<T> {

	public CooperativeEnsembleMsPacManTask() {
		this(Parameters.parameters.integerParameter("numCoevolutionSubpops"));
	}

	public CooperativeEnsembleMsPacManTask(int ensembleMembers) {
		super(ensembleMembers, true, "pacmanFitnessModeMap");
		try {
			MMNEAT.ensembleArbitrator = (MsPacManEnsembleArbitrator) ClassCreation.createObject("ensembleArbitrator");
		} catch (NoSuchMethodException ex) {
			System.out.println("Could not create ensemble arbitrator");
			System.exit(1);
		}
	}
}
