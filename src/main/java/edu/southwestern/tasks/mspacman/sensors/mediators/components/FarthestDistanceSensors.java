package edu.southwestern.tasks.mspacman.sensors.mediators.components;

import edu.southwestern.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.distance.FarthestEdibleGhostDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.distance.FarthestPillDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.distance.FarthestPowerPillDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.distance.FarthestThreatGhostDistanceBlock;

/**
 * Nearest distance to objects of interest
 *
 * @author Jacob Schrum
 */
public class FarthestDistanceSensors extends BlockLoadedInputOutputMediator {

	public FarthestDistanceSensors(boolean sensePills, boolean senseEdibleGhosts, boolean sensePowerPills) {
		super();
		blocks.add(new FarthestThreatGhostDistanceBlock());
		if (sensePills) {
			blocks.add(new FarthestPillDistanceBlock());
		}
		if (senseEdibleGhosts) {
			blocks.add(new FarthestEdibleGhostDistanceBlock());
		}
		if (sensePowerPills) {
			blocks.add(new FarthestPowerPillDistanceBlock());
		}
	}
}
