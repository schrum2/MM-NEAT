package edu.southwestern.tasks.mspacman.sensors.mediators.components;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.distancedifference.EscapeNodeDistanceDifferenceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.lookahead.DirectionalReachSafetyBeforeThreatGhostBlock;

/**
 * Forward simulation, and escape node safety
 *
 * @author Jacob Schrum
 */
public class ForwardSimulationSensors extends BlockLoadedInputOutputMediator {

	public ForwardSimulationSensors(boolean sensePills, boolean senseEdibleGhosts, boolean sensePowerPills) {
		super();
		if (Parameters.parameters.booleanParameter("staticSim")) {
			blocks.add(new EscapeNodeDistanceDifferenceBlock(escapeNodes, false, false, 0, false, false, false));
			blocks.add(new DirectionalReachSafetyBeforeThreatGhostBlock(escapeNodes));
		} else {
			boolean extra = Parameters.parameters.booleanParameter("simIncludesExtraInfo");
			blocks.add(new EscapeNodeDistanceDifferenceBlock(escapeNodes, false, false,
					Parameters.parameters.integerParameter("escapeNodeDepth"), extra && sensePills,
					extra && senseEdibleGhosts, extra && sensePowerPills));
		}
	}
}
