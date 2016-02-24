package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.combining;

import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.sensors.OneGhostAndPillsMonitorInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.OneGhostMonitorInputOutputMediator;

/**
 *
 * @author Jacob Schrum
 */
public class GhostMonitorNetworkBlock<T extends Network> extends SubNetworkBlock<T> {

    public GhostMonitorNetworkBlock(TWEANNGenotype tg, boolean includeInputs, int ghostIndex) {
        this(tg.getPhenotype(), includeInputs, ghostIndex);
    }

    public GhostMonitorNetworkBlock(Network n, boolean includeInputs, int ghostIndex) {
        super(n, Parameters.parameters.booleanParameter("ghostMonitorsSensePills") ? new OneGhostAndPillsMonitorInputOutputMediator(ghostIndex) : new OneGhostMonitorInputOutputMediator(ghostIndex), "Ghost " + ghostIndex + " Monitor", includeInputs);
    }
}
