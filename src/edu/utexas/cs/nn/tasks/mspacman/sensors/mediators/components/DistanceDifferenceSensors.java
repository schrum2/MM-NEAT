package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.components;

import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distancedifference.EdibleGhostDistanceDifferenceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distancedifference.PillDistanceDifferenceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distancedifference.PowerPillDistanceDifferenceBlock;

/**
 * directional proximity to objects of interest
 *
 * @author Jacob Schrum
 */
public class DistanceDifferenceSensors extends BlockLoadedInputOutputMediator {

    public DistanceDifferenceSensors(boolean sensePills, boolean senseEdibleGhosts, boolean sensePowerPills) {
        super();
        if (sensePills) {
            blocks.add(new PillDistanceDifferenceBlock());
        }
        if (senseEdibleGhosts) {
            blocks.add(new EdibleGhostDistanceDifferenceBlock());
        }
        if (sensePowerPills) {
            blocks.add(new PowerPillDistanceDifferenceBlock());
        }
    }
}
