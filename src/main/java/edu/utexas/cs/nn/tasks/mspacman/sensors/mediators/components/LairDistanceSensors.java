package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.components;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.lair.PacManLairDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.lair.SpecificThreatGhostLairDistanceBlock;

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
