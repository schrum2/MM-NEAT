package edu.southwestern.tasks.mspacman.sensors.blocks.combining;

import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.sensors.OneGhostAndPillsMonitorInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.OneGhostMonitorInputOutputMediator;

/**
 *
 * @author Jacob Schrum
 */
public class GhostMonitorNetworkBlock<T extends Network> extends SubNetworkBlock<T> {

	public GhostMonitorNetworkBlock(TWEANNGenotype tg, boolean includeInputs, int ghostIndex) {
		this(tg.getPhenotype(), includeInputs, ghostIndex);
	}

	public GhostMonitorNetworkBlock(Network n, boolean includeInputs, int ghostIndex) {
		super(n, Parameters.parameters.booleanParameter("ghostMonitorsSensePills")
				? new OneGhostAndPillsMonitorInputOutputMediator(ghostIndex)
				: new OneGhostMonitorInputOutputMediator(ghostIndex), "Ghost " + ghostIndex + " Monitor",
				includeInputs);
	}
}
