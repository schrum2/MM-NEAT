package edu.southwestern.tasks.mspacman.sensors.mediators.components;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.distance.lair.PacManLairDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.distance.lair.SpecificThreatGhostLairDistanceBlock;

/**
 *
 * @author Jacob Schrum
 */
public class LairDistanceSensors extends BlockLoadedInputOutputMediator {

	public LairDistanceSensors() {
		super();
		blocks.add(new PacManLairDistanceBlock());
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			blocks.add(new SpecificThreatGhostLairDistanceBlock(i));
		}
	}
}
