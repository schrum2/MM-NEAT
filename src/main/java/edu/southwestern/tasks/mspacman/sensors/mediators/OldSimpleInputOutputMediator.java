/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.mediators;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.sensors.blocks.counting.CountThreatGhostsBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.distancedifference.EscapeNodeDistanceDifferenceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.nearestfarthest.NearestFarthestEdibleGhostBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.nearestfarthest.NearestPillBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.nearestfarthest.NearestPowerPillBlock;
import edu.southwestern.tasks.mspacman.sensors.mediators.components.BaseSensors;

/**
 * The simple mediator I was using to get results with the old version of
 * pacman.
 *
 * @author Jacob Schrum
 */
public class OldSimpleInputOutputMediator extends BaseSensors {

	public OldSimpleInputOutputMediator() {
		super();
		blocks.add(new EscapeNodeDistanceDifferenceBlock(escapeNodes, false, false,
				Parameters.parameters.integerParameter("escapeNodeDepth"), true, true, true));
		blocks.add(new NearestPillBlock());
		blocks.add(new NearestPowerPillBlock());
		blocks.add(new NearestFarthestEdibleGhostBlock(true));
		blocks.add(new CountThreatGhostsBlock(true, true));
	}
}
