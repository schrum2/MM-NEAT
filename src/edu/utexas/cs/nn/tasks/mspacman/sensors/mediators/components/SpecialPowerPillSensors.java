package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.components;

import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.PowerPillAvoidanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.approaching.ApproachingPowerPillBlock;

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
