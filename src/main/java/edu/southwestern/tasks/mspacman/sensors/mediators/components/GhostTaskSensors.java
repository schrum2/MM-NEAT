package edu.southwestern.tasks.mspacman.sensors.mediators.components;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.SpecificGhostBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.SplitSpecificGhostBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.distance.FarthestThreatGhostDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.distance.NearestPowerPillDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.distance.NearestThreatGhostDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.distancedifference.EscapeNodeDistanceDifferenceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.nearestfarthest.NearestPowerPillBlock;

/**
 * !!! SHOULD MOVE THIS OUT OF COMPONENTS AND JUST MAKE IT A MEDIATOR !!!!!
 *
 * Contains only sensor blocks that are absolutely necessary to all other
 * mediators.
 *
 * @author Jacob Schrum
 */
public class GhostTaskSensors extends BlockLoadedInputOutputMediator {

	public GhostTaskSensors() {
		super();
		boolean specificGhostEdibleThreatSplit = Parameters.parameters
				.booleanParameter("specificGhostEdibleThreatSplit");
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			blocks.add(specificGhostEdibleThreatSplit ? new SplitSpecificGhostBlock(i) : new SpecificGhostBlock(i));
		}

		blocks.add(new EscapeNodeDistanceDifferenceBlock(escapeNodes, false, false,
				Parameters.parameters.integerParameter("escapeNodeDepth"), false, true, true));
		blocks.add(new NearestPowerPillBlock());
		blocks.add(new NearestPowerPillDistanceBlock());
		if (CommonConstants.numActiveGhosts > 1) {
			blocks.add(new NearestThreatGhostDistanceBlock());
			blocks.add(new FarthestThreatGhostDistanceBlock());
		}
	}
}
