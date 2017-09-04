package edu.southwestern.tasks.mspacman.sensors.mediators.components;

import edu.southwestern.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.nearestfarthest.NearestFarthestEdibleGhostBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.nearestfarthest.NearestFarthestThreatGhostBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.nearestfarthest.NearestPillBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.nearestfarthest.NearestPowerPillBlock;

/**
 * Nearest direction to objects of interest
 *
 * @author Jacob Schrum
 */
public class NearestDirectionSensors extends BlockLoadedInputOutputMediator {

	public NearestDirectionSensors(boolean sensePills, boolean senseEdibleGhosts, boolean sensePowerPills) {
		super();
		blocks.add(new NearestFarthestThreatGhostBlock(true));
		if (sensePills) {
			blocks.add(new NearestPillBlock());
		}
		if (senseEdibleGhosts) {
			blocks.add(new NearestFarthestEdibleGhostBlock(true));
		}
		if (sensePowerPills) {
			blocks.add(new NearestPowerPillBlock());
		}
	}
}
