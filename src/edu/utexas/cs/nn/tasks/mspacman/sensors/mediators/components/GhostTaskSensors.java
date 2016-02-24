package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.components;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.SpecificGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.SplitSpecificGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.FarthestThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.NearestPowerPillDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.NearestThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distancedifference.EscapeNodeDistanceDifferenceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.nearestfarthest.NearestPowerPillBlock;

/**
 * !!! SHOULD MOVE THIS OUT OF COMPONENTS AND JUST MAKE IT A MEDIATOR !!!!!
 *
 * Contains only sensor blocks that are absolutely necessary to all other
 * mediators.
 *
 * @author Jacob Schrum
 */
public class GhostTaskSensors extends BlockLoadedInputOutputMediator {

    public GhostTaskSensors() {
        super();
        boolean specificGhostEdibleThreatSplit = Parameters.parameters.booleanParameter("specificGhostEdibleThreatSplit");
        for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
            blocks.add(specificGhostEdibleThreatSplit ? new SplitSpecificGhostBlock(i) : new SpecificGhostBlock(i));
        }

        blocks.add(new EscapeNodeDistanceDifferenceBlock(escapeNodes, false, false, Parameters.parameters.integerParameter("escapeNodeDepth"), false, true, true));
        blocks.add(new NearestPowerPillBlock());
        blocks.add(new NearestPowerPillDistanceBlock());
        if (CommonConstants.numActiveGhosts > 1) {
            blocks.add(new NearestThreatGhostDistanceBlock());
            blocks.add(new FarthestThreatGhostDistanceBlock());
        }
    }
}
