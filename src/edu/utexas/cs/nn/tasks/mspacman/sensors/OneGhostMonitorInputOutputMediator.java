package edu.utexas.cs.nn.tasks.mspacman.sensors;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.SpecificGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.SplitSpecificGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;

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
        boolean specificGhostEdibleThreatSplit = Parameters.parameters.booleanParameter("specificGhostEdibleThreatSplit");
        blocks.add(new BiasBlock());
        blocks.add(specificGhostEdibleThreatSplit ? new SplitSpecificGhostBlock(ghostIndex) : new SpecificGhostBlock(ghostIndex));
    }
}
