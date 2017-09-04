package edu.southwestern.tasks.mspacman.sensors.mediators.components;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.distance.NearestPillDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.distance.NearestPowerPillDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.distancedifference.EscapeNodeDistanceDifferenceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.nearestfarthest.NearestPillBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.nearestfarthest.NearestPowerPillBlock;

/**
 * Components that do not sense ghosts. To be combined with Ghost Monitors
 *
 * @author Jacob Schrum
 */
public class NonGhostSensors extends BlockLoadedInputOutputMediator {

	public NonGhostSensors() {
		super();
		boolean ignorePillScore = Parameters.parameters.booleanParameter("ignorePillScore");
		boolean noPowerPills = Parameters.parameters.booleanParameter("noPowerPills");

		blocks.add(new EscapeNodeDistanceDifferenceBlock(escapeNodes, false, false,
				Parameters.parameters.integerParameter("escapeNodeDepth"), !ignorePillScore, !noPowerPills,
				!noPowerPills));
		if (!noPowerPills) {
			blocks.add(new NearestPowerPillBlock());
			blocks.add(new NearestPowerPillDistanceBlock());
		}
		if (!ignorePillScore) {
			blocks.add(new NearestPillBlock());
			blocks.add(new NearestPillDistanceBlock());
		}
	}
}
