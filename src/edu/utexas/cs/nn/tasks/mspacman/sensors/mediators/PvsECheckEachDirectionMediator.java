/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.AnyEdibleGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting.PillsRemainingBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionCountJunctionOptionsBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts.VariableDirectionKStepJunctionCountBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts.VariableDirectionKStepPillCountBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.VariableDirectionJunctionDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.VariableDirectionPillDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionSortedGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.specific.VariableDirectionSpecificGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.specific.VariableDirectionSortedGhostIncomingBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.specific.VariableDirectionSpecificGhostIncomingBlock;

/**
 *
 * @author Jacob Schrum
 */
public class PvsECheckEachDirectionMediator extends VariableDirectionBlockLoadedInputOutputMediator {

    public PvsECheckEachDirectionMediator() {
        int direction = -1;
        int numJunctionsToSense = Parameters.parameters.integerParameter("junctionsToSense");

        blocks.add(new BiasBlock());
        // Distances
        blocks.add(new VariableDirectionPillDistanceBlock(direction));
        for (int i = 0; i < numJunctionsToSense; i++) {
            blocks.add(new VariableDirectionJunctionDistanceBlock(direction, i));
        }
        // Specific Ghosts
        if (Parameters.parameters.booleanParameter("specific")) {
            for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
                blocks.add(new VariableDirectionSpecificGhostDistanceBlock(direction, i));
                blocks.add(new VariableDirectionSpecificGhostIncomingBlock(i));
            }
        }
        // Ghosts By Distance
        if (Parameters.parameters.booleanParameter("specificGhostProximityOrder")) {
            int proxGhostsToSense = Parameters.parameters.integerParameter("proxGhostsToSense");
            for (int i = 0; i < CommonConstants.numActiveGhosts && i < proxGhostsToSense; i++) {
                blocks.add(new VariableDirectionSortedGhostDistanceBlock(i));
                blocks.add(new VariableDirectionSortedGhostIncomingBlock(i));
            }
        }
        // Look ahead
        blocks.add(new VariableDirectionKStepPillCountBlock(direction));
        blocks.add(new VariableDirectionKStepJunctionCountBlock(direction));
        // Other
        blocks.add(new PillsRemainingBlock(true, false));
        blocks.add(new AnyEdibleGhostBlock());
        // High level
        //blocks.add(new VariableDirectionDistanceFromJunctionToGhostBlock(direction, true, true));
        //blocks.add(new VariableDirectionCountAllPillsInKStepsBlock(direction));
        if(Parameters.parameters.booleanParameter("highLevel")) {
            blocks.add(new VariableDirectionCountJunctionOptionsBlock());
        }
        //blocks.add(new VariableDirectionOneStepSafeBlock());
    }
}
