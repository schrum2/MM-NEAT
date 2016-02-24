package edu.utexas.cs.nn.tasks.mspacman;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.ensemble.MsPacManEnsembleArbitrator;
import edu.utexas.cs.nn.util.ClassCreation;

/**
 * Several sub-network populations are evolved and on each time step, some
 * ensemble arbitrator method picks an action based on the outputs of all
 * subnetworks.
 *
 * @author Jacob Schrum
 */
public class CooperativeEnsembleMsPacManTask<T extends Network> extends CooperativeNonHierarchicalMultiNetMsPacManTask<T> {

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
