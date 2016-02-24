/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting.*;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.FarthestThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.NearestThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.time.EdibleTimesBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionCountJunctionOptionsBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionLastActivationBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionLastDirectionBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionPillsBeforeJunctionBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.blocking.*;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts.*;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.VariableDirectionJunctionDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.VariableDirectionPillDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.VariableDirectionPowerPillDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.farthest.VariableDirectionFarthestEdibleGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.farthest.VariableDirectionFarthestIncomingEdibleGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.farthest.VariableDirectionFarthestIncomingThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.farthest.VariableDirectionFarthestThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.fromjunction.VariableDirectionDistanceFromJunctionToGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.fromjunction.VariableDirectionDistanceFromJunctionToIncomingGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionEdibleGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.incoming.VariableDirectionIncomingEdibleGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.incoming.VariableDirectionIncomingThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.scent.VariableDirectionKStepDeathScentBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.scent.VariableDirectionPersonalScentBlock;

/**
 * Mediator is designed to be used with a quality safety check function, and
 * therefore lacks many of the sophisticated safety check sensors
 *
 * @author Jacob Schrum
 */
public class SafeCheckEachDirectionMediator extends VariableDirectionBlockLoadedInputOutputMediator {

    public SafeCheckEachDirectionMediator() {
        this(-1); // direction irrelevant
    }

    public SafeCheckEachDirectionMediator(int direction) {
        boolean incoming = Parameters.parameters.booleanParameter("incoming");
        boolean communalDeathMemory = Parameters.parameters.booleanParameter("communalDeathMemory");
        boolean farthestDistances = Parameters.parameters.booleanParameter("farthestDis");

        blocks.add(new BiasBlock());
        // Distances
        blocks.add(new VariableDirectionPillDistanceBlock(direction));
        blocks.add(new VariableDirectionPowerPillDistanceBlock(direction));
        blocks.add(new VariableDirectionEdibleGhostDistanceBlock(direction));
        if (farthestDistances) {
            blocks.add(new VariableDirectionFarthestEdibleGhostDistanceBlock(direction));
        }
        blocks.add(new VariableDirectionThreatGhostDistanceBlock(direction));
        if (farthestDistances) {
            blocks.add(new VariableDirectionFarthestThreatGhostDistanceBlock(direction));
        }
        blocks.add(new VariableDirectionJunctionDistanceBlock(direction));
        blocks.add(new VariableDirectionDistanceFromJunctionToGhostBlock(direction, true, false));
        blocks.add(new VariableDirectionDistanceFromJunctionToGhostBlock(direction, false, true));
        if (incoming) {
            blocks.add(new VariableDirectionIncomingEdibleGhostDistanceBlock(direction));
            blocks.add(new VariableDirectionIncomingThreatGhostDistanceBlock(direction));
            blocks.add(new VariableDirectionDistanceFromJunctionToIncomingGhostBlock(direction, true));
            blocks.add(new VariableDirectionDistanceFromJunctionToIncomingGhostBlock(direction, false));
            if (farthestDistances) {
                blocks.add(new VariableDirectionFarthestIncomingEdibleGhostDistanceBlock(direction));
                blocks.add(new VariableDirectionFarthestIncomingThreatGhostDistanceBlock(direction));
            }
        }
        // Obstructions
        blocks.add(new VariableDirectionThreatGhostBlocksJunctionBlock());
        blocks.add(new VariableDirectionThreatGhostBlocksEdibleGhostBlock());
        blocks.add(new VariableDirectionPowerPillBlocksThreatGhostBlock());
        blocks.add(new VariableDirectionPowerPillBlocksEdibleGhostBlock());
        blocks.add(new VariableDirectionPowerPillBlocksJunctionBlock());
        blocks.add(new VariableDirectionCountJunctionOptionsBlock());
        // Look ahead
        blocks.add(new VariableDirectionKStepPillCountBlock(direction));
        blocks.add(new VariableDirectionKStepPowerPillCountBlock(direction));
        blocks.add(new VariableDirectionKStepEdibleGhostCountBlock(direction, true));
        blocks.add(new VariableDirectionKStepEdibleGhostCountBlock(direction, false));
        blocks.add(new VariableDirectionKStepThreatGhostCountBlock(direction, true));
        blocks.add(new VariableDirectionKStepThreatGhostCountBlock(direction, false));
        blocks.add(new VariableDirectionKStepJunctionCountBlock(direction));
        if (incoming) {
            blocks.add(new VariableDirectionKStepIncomingEdibleGhostCountBlock(direction, true));
            blocks.add(new VariableDirectionKStepIncomingEdibleGhostCountBlock(direction, false));
            blocks.add(new VariableDirectionKStepIncomingThreatGhostCountBlock(direction, true));
            blocks.add(new VariableDirectionKStepIncomingThreatGhostCountBlock(direction, false));
        }
        if (communalDeathMemory) {
            blocks.add(new VariableDirectionKStepDeathScentBlock(direction, true));
            blocks.add(new VariableDirectionKStepDeathScentBlock(direction, false));
        }
        blocks.add(new VariableDirectionCountAllPillsInKStepsBlock(direction));
        // Misc
        blocks.add(new EdibleTimesBlock(new boolean[]{false, false, false, true}));
        blocks.add(new VariableDirectionPillsBeforeJunctionBlock(direction));
        blocks.add(new NearestThreatGhostDistanceBlock());
        if (farthestDistances) {
            blocks.add(new FarthestThreatGhostDistanceBlock());
        }
        blocks.add(new CountLairGhostsBlock(true, false));
        blocks.add(new CountEdibleGhostsBlock(true, false));
        blocks.add(new CountThreatGhostsBlock(true, false));
        blocks.add(new PillsRemainingBlock(true, false));
        blocks.add(new PowerPillsRemainingBlock(true, false));
        // Tracking own behavior
        if (Parameters.parameters.booleanParameter("previousPreferences")) {
            blocks.add(new VariableDirectionLastDirectionBlock(direction));
            for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
                blocks.add(new VariableDirectionLastActivationBlock(direction, i));
            }
        }
        if (Parameters.parameters.booleanParameter("personalScent")) {
            blocks.add(new VariableDirectionPersonalScentBlock(direction));
        }
    }
}
