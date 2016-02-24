/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators;

import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.specific.VariableDirectionSpecificApproachingEdibleGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.specific.VariableDirectionSpecificEdibleGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.specific.VariableDirectionSpecificApproachingThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.specific.VariableDirectionSpecificThreatGhostDistanceBlock;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.FarthestThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.NearestThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.SpecificGhostDistancesBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts.*;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.*;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.reachfirst.*;        
        
/**
 * Just like the Alternate Check Each Direction Mediator, but has additional
 * sensors that focus specifically on the 4th ghost, which in the Legacy team
 * is the one that moves randomly.
 *
 * @author Jacob Schrum
 */
public class RandomFocusCheckEachDirectionMediator extends AlternateCheckEachDirectionMediator {

    public RandomFocusCheckEachDirectionMediator() {
        super();
        boolean incoming = Parameters.parameters.booleanParameter("incoming");
        int irrelevantStartingDirection = -1;
        int randomGhostIndex = 3;
        boolean[] mask = new boolean[]{false,false,false,true};

        // Distances
        blocks.add(new VariableDirectionSpecificEdibleGhostDistanceBlock(irrelevantStartingDirection, randomGhostIndex));
        blocks.add(new VariableDirectionSpecificThreatGhostDistanceBlock(irrelevantStartingDirection, randomGhostIndex));
        if (incoming) {
            blocks.add(new VariableDirectionSpecificApproachingEdibleGhostDistanceBlock(irrelevantStartingDirection, randomGhostIndex));
            blocks.add(new VariableDirectionSpecificApproachingThreatGhostDistanceBlock(irrelevantStartingDirection, randomGhostIndex));
        }
        // Obstructions
        blocks.add(new VariableDirectionCloserToJunctionThanThreatGhostBlock(irrelevantStartingDirection, new int[]{randomGhostIndex}));
        blocks.add(new VariableDirectionCloserToDepthTwoJunctionThanThreatGhostBlock(irrelevantStartingDirection, new int[]{randomGhostIndex}));
        blocks.add(new VariableDirectionCloserToPowerPillThanThreatGhostBlock(irrelevantStartingDirection, new int[]{randomGhostIndex}));
        blocks.add(new VariableDirectionCloserToEdibleGhostThanThreatGhostBlock(irrelevantStartingDirection, new int[]{randomGhostIndex})); // Does not account for edible ghost movement
        blocks.add(new VariableDirectionPowerPillBeforeEdibleGhostBlock(irrelevantStartingDirection, mask));
        // Look ahead
        blocks.add(new VariableDirectionKStepEdibleGhostCountBlock(irrelevantStartingDirection, mask, true));
        blocks.add(new VariableDirectionKStepThreatGhostCountBlock(irrelevantStartingDirection, mask, true));
        if (incoming) {
            blocks.add(new VariableDirectionKStepIncomingEdibleGhostCountBlock(irrelevantStartingDirection, mask, true));
            blocks.add(new VariableDirectionKStepIncomingThreatGhostCountBlock(irrelevantStartingDirection, true, mask));
            blocks.add(new VariableDirectionKStepIncomingThreatGhostCountBlock(irrelevantStartingDirection, false, mask));
        }
        // Misc
        blocks.add(new SpecificGhostDistancesBlock(mask, true, false));
    }
}
