/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting.CountLairGhostsBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionCountJunctionOptionsBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionPillsBeforeJunctionBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.blocking.VariableDirectionPowerPillBlocksEdibleGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.blocking.VariableDirectionPowerPillBlocksJunctionBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.blocking.VariableDirectionPowerPillBlocksThreatGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts.*;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.VariableDirectionPillDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.VariableDirectionPowerPillDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.frompowerpill.VariableDirectionAverageDistanceFromPowerPillToThreatGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.frompowerpill.VariableDirectionMaxDistanceFromPowerPillToThreatGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.frompowerpill.VariableDirectionMinDistanceFromPowerPillToThreatGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionEdibleGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.incoming.VariableDirectionIncomingEdibleGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.incoming.VariableDirectionIncomingThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.opposite.VariableDirectionOtherDirectionsStatisticBlock;
import edu.utexas.cs.nn.util.stats.Max;
import edu.utexas.cs.nn.util.stats.Min;

/**
 *
 * @author Jacob Schrum
 */
public class TweakCheckEachDirectionMediator extends VariableDirectionBlockLoadedInputOutputMediator {

    public TweakCheckEachDirectionMediator() {
        int direction = -1;
        blocks.add(new BiasBlock());
        // Distances
        blocks.add(new VariableDirectionPillDistanceBlock(direction));
        blocks.add(new VariableDirectionPowerPillDistanceBlock(direction));
        blocks.add(new VariableDirectionEdibleGhostDistanceBlock(direction));
        blocks.add(new VariableDirectionThreatGhostDistanceBlock(direction));
        blocks.add(new VariableDirectionIncomingEdibleGhostDistanceBlock(direction));
        blocks.add(new VariableDirectionIncomingThreatGhostDistanceBlock(direction));
        blocks.add(new VariableDirectionKStepPillCountBlock(direction));
        blocks.add(new VariableDirectionKStepEdibleGhostCountBlock(direction, true));
        blocks.add(new VariableDirectionKStepEdibleGhostCountBlock(direction, false));
        blocks.add(new VariableDirectionKStepThreatGhostCountBlock(direction, true));
        blocks.add(new VariableDirectionKStepThreatGhostCountBlock(direction, false));
        blocks.add(new VariableDirectionKStepJunctionCountBlock(direction));
        blocks.add(new VariableDirectionMaxDistanceFromPowerPillToThreatGhostBlock(direction));
        blocks.add(new VariableDirectionMinDistanceFromPowerPillToThreatGhostBlock(direction));
        blocks.add(new VariableDirectionAverageDistanceFromPowerPillToThreatGhostBlock(direction));
        blocks.add(new VariableDirectionCountJunctionOptionsBlock());
        blocks.add(new VariableDirectionPowerPillBlocksThreatGhostBlock());
        blocks.add(new VariableDirectionPowerPillBlocksEdibleGhostBlock());
        blocks.add(new VariableDirectionPowerPillBlocksJunctionBlock());
        blocks.add(new CountLairGhostsBlock(true, false));
        blocks.add(new VariableDirectionCountAllPillsInKStepsBlock(direction));
        blocks.add(new VariableDirectionPillsBeforeJunctionBlock(direction));
        boolean otherDirections = Parameters.parameters.booleanParameter("otherDirSensors");
        if(otherDirections) {
            // Min distances
            blocks.add(new VariableDirectionOtherDirectionsStatisticBlock(new VariableDirectionPillDistanceBlock(direction), new Min()));
            blocks.add(new VariableDirectionOtherDirectionsStatisticBlock(new VariableDirectionEdibleGhostDistanceBlock(direction), new Min()));
            blocks.add(new VariableDirectionOtherDirectionsStatisticBlock(new VariableDirectionThreatGhostDistanceBlock(direction), new Min()));
            blocks.add(new VariableDirectionOtherDirectionsStatisticBlock(new VariableDirectionIncomingEdibleGhostDistanceBlock(direction), new Min()));
            blocks.add(new VariableDirectionOtherDirectionsStatisticBlock(new VariableDirectionIncomingThreatGhostDistanceBlock(direction), new Min()));
            // Max counts
            blocks.add(new VariableDirectionOtherDirectionsStatisticBlock(new VariableDirectionKStepPillCountBlock(direction), new Max()));
            blocks.add(new VariableDirectionOtherDirectionsStatisticBlock(new VariableDirectionKStepEdibleGhostCountBlock(direction, true), new Max()));
            blocks.add(new VariableDirectionOtherDirectionsStatisticBlock(new VariableDirectionKStepThreatGhostCountBlock(direction, true), new Max()));
            blocks.add(new VariableDirectionOtherDirectionsStatisticBlock(new VariableDirectionKStepJunctionCountBlock(direction), new Max()));
            blocks.add(new VariableDirectionOtherDirectionsStatisticBlock(new VariableDirectionCountAllPillsInKStepsBlock(direction), new Max()));
        }
    }
}
