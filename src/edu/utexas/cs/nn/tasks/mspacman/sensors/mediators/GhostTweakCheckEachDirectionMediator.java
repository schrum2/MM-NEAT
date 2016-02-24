/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators;

import edu.utexas.cs.nn.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting.CountLairGhostsBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.blocking.VariableDirectionPowerPillBlocksEdibleGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.blocking.VariableDirectionPowerPillBlocksJunctionBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.blocking.VariableDirectionPowerPillBlocksThreatGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.blocking.VariableDirectionThreatGhostBlocksJunctionBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts.VariableDirectionKStepEdibleGhostCountBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts.VariableDirectionKStepJunctionCountBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts.VariableDirectionKStepThreatGhostCountBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.*;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.frompowerpill.VariableDirectionAverageDistanceFromPowerPillToThreatGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.frompowerpill.VariableDirectionMaxDistanceFromPowerPillToThreatGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.frompowerpill.VariableDirectionMinDistanceFromPowerPillToThreatGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionEdibleGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.incoming.VariableDirectionIncomingEdibleGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.incoming.VariableDirectionIncomingThreatGhostDistanceBlock;

/**
 *
 * @author Jacob Schrum
 */
public class GhostTweakCheckEachDirectionMediator extends VariableDirectionBlockLoadedInputOutputMediator {

    public GhostTweakCheckEachDirectionMediator() {
        int direction = -1;
        blocks.add(new BiasBlock());
        // Distances
        blocks.add(new VariableDirectionPowerPillDistanceBlock(direction));
        blocks.add(new VariableDirectionEdibleGhostDistanceBlock(direction));
        blocks.add(new VariableDirectionThreatGhostDistanceBlock(direction));
        blocks.add(new VariableDirectionIncomingEdibleGhostDistanceBlock(direction));
        blocks.add(new VariableDirectionIncomingThreatGhostDistanceBlock(direction));
        blocks.add(new VariableDirectionJunctionDistanceBlock(direction));
        // Count
        blocks.add(new VariableDirectionKStepEdibleGhostCountBlock(direction, true));
        blocks.add(new VariableDirectionKStepThreatGhostCountBlock(direction, true));
        blocks.add(new VariableDirectionKStepJunctionCountBlock(direction));
        // From Power Pill
        blocks.add(new VariableDirectionMaxDistanceFromPowerPillToThreatGhostBlock(direction));
        blocks.add(new VariableDirectionMinDistanceFromPowerPillToThreatGhostBlock(direction));
        blocks.add(new VariableDirectionAverageDistanceFromPowerPillToThreatGhostBlock(direction));
        
        //blocks.add(new VariableDirectionCountJunctionOptionsBlock());
        
        // Obstructions
        blocks.add(new VariableDirectionThreatGhostBlocksJunctionBlock());
        blocks.add(new VariableDirectionPowerPillBlocksThreatGhostBlock());
        blocks.add(new VariableDirectionPowerPillBlocksEdibleGhostBlock());
        blocks.add(new VariableDirectionPowerPillBlocksJunctionBlock());
        blocks.add(new CountLairGhostsBlock(true, false));
        
//        int randomGhostIndex = 3;
//        blocks.add(new VariableDirectionSpecificEdibleGhostDistanceBlock(direction, randomGhostIndex));
//        blocks.add(new VariableDirectionSpecificThreatGhostDistanceBlock(direction, randomGhostIndex));
//        blocks.add(new VariableDirectionSpecificIncomingEdibleGhostDistanceBlock(direction, randomGhostIndex));
//        blocks.add(new VariableDirectionSpecificIncomingThreatGhostDistanceBlock(direction, randomGhostIndex));
    }
}
