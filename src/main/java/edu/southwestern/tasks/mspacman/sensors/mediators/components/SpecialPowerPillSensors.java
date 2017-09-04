package edu.southwestern.tasks.mspacman.sensors.mediators.components;

import edu.southwestern.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.PowerPillAvoidanceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.approaching.ApproachingPowerPillBlock;

/**
 * Contains only sensor blocks that are absolutely necessary to all other
 * mediators.
 *
 * @author Jacob Schrum
 */
public class SpecialPowerPillSensors extends BlockLoadedInputOutputMediator {

	public SpecialPowerPillSensors() {
		super();
		blocks.add(new ApproachingPowerPillBlock());
		blocks.add(new PowerPillAvoidanceBlock());
	}
}
