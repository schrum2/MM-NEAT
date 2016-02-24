/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.AnyEdibleGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts.VariableDirectionKStepJunctionCountBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.VariableDirectionJunctionDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.fromjunction.VariableDirectionDistanceFromJunctionToGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.specific.VariableDirectionSpecificGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.specific.VariableDirectionSpecificGhostIncomingBlock;

/**
 *
 * @author Jacob Schrum
 */
public class TvsECheckEachDirectionMediator extends VariableDirectionBlockLoadedInputOutputMediator {

    public TvsECheckEachDirectionMediator() {
        int irrelevantStartingDirection = -1;

        blocks.add(new BiasBlock());
        // Distances
        blocks.add(new VariableDirectionJunctionDistanceBlock(irrelevantStartingDirection));
        // Specific Ghosts
        for(int i = 0; i < CommonConstants.numActiveGhosts; i++) {
            blocks.add(new VariableDirectionSpecificGhostDistanceBlock(irrelevantStartingDirection, i));
            blocks.add(new VariableDirectionSpecificGhostIncomingBlock(i));
            //blocks.add(new SpecificGhostIsEdibleBlock(i));
        }
        // Look ahead
        blocks.add(new VariableDirectionDistanceFromJunctionToGhostBlock(irrelevantStartingDirection, true, true));
        blocks.add(new AnyEdibleGhostBlock());
        blocks.add(new VariableDirectionKStepJunctionCountBlock(irrelevantStartingDirection));
    }
}
