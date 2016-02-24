package edu.utexas.cs.nn.tasks.mspacman;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.ensemble.MsPacManEnsembleArbitrator;
import edu.utexas.cs.nn.util.ClassCreation;

/**
 * @author Jacob Schrum
 */
public class CooperativeGhostMonitorEnsembleMsPacManTask<T extends Network> extends CooperativeNonHierarchicalMultiNetMsPacManTask<T> {

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
