package edu.southwestern.tasks.mspacman.sensors.mediators;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.SpecificGhostIsEdibleBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.coords.SpecificGhostXOffsetBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.coords.SpecificGhostYOffsetBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.distance.WallDistanceBlock;

/**
 *
 * @author Jacob Schrum
 */
public class ImpoverishedGhostTaskMediator extends BlockLoadedInputOutputMediator {

	public ImpoverishedGhostTaskMediator() {
		blocks.add(new BiasBlock());
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			blocks.add(new SpecificGhostXOffsetBlock(i));
			blocks.add(new SpecificGhostYOffsetBlock(i));
			// blocks.add(new SpecificGhostEdibleTimeBlock(i));
			// blocks.add(new SpecificGhostLairTimeBlock(i));
			blocks.add(new SpecificGhostIsEdibleBlock(i));
		}
		blocks.add(new WallDistanceBlock());
	}
}
