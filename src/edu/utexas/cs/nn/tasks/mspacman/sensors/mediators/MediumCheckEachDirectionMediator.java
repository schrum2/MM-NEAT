/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators;

import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionEdibleGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts.*;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.*;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.reachfirst.VariableDirectionCloserToJunctionThanThreatGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.reachfirst.VariableDirectionCloserToPowerPillThanThreatGhostBlock;

/**
 * Based on Brandstetter's CIG 2012 paper
 *
 * @author Jacob Schrum
 */
public class MediumCheckEachDirectionMediator extends VariableDirectionBlockLoadedInputOutputMediator {

    public MediumCheckEachDirectionMediator() {
        int irrelevantStartingDirection = -1;
        // Distances
        blocks.add(new VariableDirectionPillDistanceBlock(irrelevantStartingDirection));
        blocks.add(new VariableDirectionPowerPillDistanceBlock(irrelevantStartingDirection));
        blocks.add(new VariableDirectionEdibleGhostDistanceBlock(irrelevantStartingDirection));
        blocks.add(new VariableDirectionThreatGhostDistanceBlock(irrelevantStartingDirection));
        blocks.add(new VariableDirectionJunctionDistanceBlock(irrelevantStartingDirection));
        // Obstructions
        //blocks.add(new VariableDirectionThreatGhostBlocksJunctionBlock());
        //blocks.add(new VariableDirectionPowerPillBlocksThreatGhostBlock());
        // Look ahead
        blocks.add(new VariableDirectionKStepPillCountBlock(irrelevantStartingDirection));
        blocks.add(new VariableDirectionKStepPowerPillCountBlock(irrelevantStartingDirection));
        blocks.add(new VariableDirectionKStepEdibleGhostCountBlock(irrelevantStartingDirection, true));
        blocks.add(new VariableDirectionKStepThreatGhostCountBlock(irrelevantStartingDirection, true));
        blocks.add(new VariableDirectionKStepJunctionCountBlock(irrelevantStartingDirection));
        // Extra
        blocks.add(new VariableDirectionCloserToJunctionThanThreatGhostBlock(irrelevantStartingDirection));
        blocks.add(new VariableDirectionCloserToPowerPillThanThreatGhostBlock(irrelevantStartingDirection));
    }
}
