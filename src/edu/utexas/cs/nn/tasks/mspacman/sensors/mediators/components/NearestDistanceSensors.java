package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.components;

import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.NearestEdibleGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.NearestPillDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.NearestPowerPillDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.NearestThreatGhostDistanceBlock;

/**
 * Nearest distance to objects of interest
 *
 * @author Jacob Schrum
 */
public class NearestDistanceSensors extends BlockLoadedInputOutputMediator {

    public NearestDistanceSensors(boolean sensePills, boolean senseEdibleGhosts, boolean sensePowerPIlls) {
        super();
        //blocks.add(new NearestJunctionDistanceBlock());
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
