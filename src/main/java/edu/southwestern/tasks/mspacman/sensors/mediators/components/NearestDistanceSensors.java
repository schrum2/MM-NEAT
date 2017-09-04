package edu.southwestern.tasks.mspacman.sensors.mediators.components;

import edu.southwestern.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.distance.NearestEdibleGhostDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.distance.NearestPillDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.distance.NearestPowerPillDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.distance.NearestThreatGhostDistanceBlock;

/**
 * Nearest distance to objects of interest
 *
 * @author Jacob Schrum
 */
public class NearestDistanceSensors extends BlockLoadedInputOutputMediator {

	public NearestDistanceSensors(boolean sensePills, boolean senseEdibleGhosts, boolean sensePowerPIlls) {
		super();
		// blocks.add(new NearestJunctionDistanceBlock());
		blocks.add(new NearestThreatGhostDistanceBlock());
		if (sensePills) {
			blocks.add(new NearestPillDistanceBlock());
		}
		if (senseEdibleGhosts) {
			blocks.add(new NearestEdibleGhostDistanceBlock());
		}
		if (sensePowerPIlls) {
			blocks.add(new NearestPowerPillDistanceBlock());
		}
	}
}
