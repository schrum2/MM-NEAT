package edu.southwestern.tasks.mspacman.sensors;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.sensors.blocks.SpecificGhostBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.SplitSpecificGhostBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;

/**
 * Monitors a single ghost
 *
 * @author Jacob Schrum
 */
public class OneGhostMonitorInputOutputMediator extends BlockLoadedInputOutputMediator {

	// Should only be used by ClassCreation
	public OneGhostMonitorInputOutputMediator() {
		this(0);
	}

	public OneGhostMonitorInputOutputMediator(int ghostIndex) {
		super();
		boolean specificGhostEdibleThreatSplit = Parameters.parameters
				.booleanParameter("specificGhostEdibleThreatSplit");
		blocks.add(new BiasBlock());
		blocks.add(specificGhostEdibleThreatSplit ? new SplitSpecificGhostBlock(ghostIndex)
				: new SpecificGhostBlock(ghostIndex));
	}
}
