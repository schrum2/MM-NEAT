/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.AllThreatsPresentBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.AnyEdibleGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.veryclose.IsCloseToPowerPill;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting.CountEdibleGhostsBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting.PillsRemainingBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting.PowerPillsRemainingBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.time.EdibleTimesBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionCountJunctionOptionsBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.blocking.VariableDirectionGhostBlocksJunctionBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.blocking.VariableDirectionPowerPillBlocksJunctionBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts.VariableDirectionKStepJunctionCountBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts.VariableDirectionKStepPillCountBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.VariableDirectionJunctionDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.VariableDirectionPillDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.VariableDirectionPowerPillDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionSortedGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionSortedGhostEdibleTimeVsDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.specific.VariableDirectionSpecificGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.specific.VariableDirectionSortedGhostEdibleBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.specific.VariableDirectionSortedGhostIncomingBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.specific.VariableDirectionSortedGhostTrappedBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.specific.VariableDirectionSpecificGhostIncomingBlock;

/**
 * Mediator for the infinite imprison task (though has been extended beyond that). 
 * Of interest: One sensor for whether ghosts are edible or not, which simplifies
 * things; incoming ghost sensors can be turned on or off; Can sense if all threats
 * are present (which should inform decision to eat power pill); can sense if very
 * close to power pill.
 * 
 * @author Jacob Schrum
 */
public class IICheckEachDirectionMediator extends VariableDirectionBlockLoadedInputOutputMediator {

    public IICheckEachDirectionMediator() {
        int direction = -1;
        int numJunctionsToSense = Parameters.parameters.integerParameter("junctionsToSense");
        boolean incoming = Parameters.parameters.booleanParameter("incoming");
        boolean infiniteEdibleTime = Parameters.parameters.booleanParameter("infiniteEdibleTime");
        boolean imprisonedWhileEdible = Parameters.parameters.booleanParameter("imprisonedWhileEdible");
        
        blocks.add(new BiasBlock());
        // Distances
        blocks.add(new VariableDirectionPillDistanceBlock(direction));
        blocks.add(new VariableDirectionPowerPillDistanceBlock(direction));
        for (int i = 0; i < numJunctionsToSense; i++) {
            blocks.add(new VariableDirectionJunctionDistanceBlock(direction, i));
        }
        // Specific Ghosts: Probably never use again
        if (Parameters.parameters.booleanParameter("specific")) {
            for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
                blocks.add(new VariableDirectionSpecificGhostDistanceBlock(direction, i));
                if (incoming) {
                    blocks.add(new VariableDirectionSpecificGhostIncomingBlock(i));
                }
                //blocks.add(new SpecificGhostIsEdibleBlock(i));
            }
        }
        // Ghosts By Distance
        if (Parameters.parameters.booleanParameter("specificGhostProximityOrder")) {
            for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
                blocks.add(new VariableDirectionSortedGhostDistanceBlock(i));
                if (incoming) {
                    blocks.add(new VariableDirectionSortedGhostIncomingBlock(i));
                }
                if(Parameters.parameters.booleanParameter("trapped")) {
                    blocks.add(new VariableDirectionSortedGhostTrappedBlock(i));
                }
                if(Parameters.parameters.booleanParameter("eTimeVsGDis")) {
                    blocks.add(new VariableDirectionSortedGhostEdibleTimeVsDistanceBlock(i));
                }
                if(!imprisonedWhileEdible) {
                    blocks.add(new VariableDirectionSortedGhostEdibleBlock(i));
                }
            }
        }
        // Split ghost sensors: edible and threat
        boolean split = Parameters.parameters.booleanParameter("specificGhostEdibleThreatSplit");
        if(split){
            for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
                // Threat prox
                blocks.add(new VariableDirectionSortedGhostDistanceBlock(-1,i,false,false));
                // Edible prox
                blocks.add(new VariableDirectionSortedGhostDistanceBlock(-1,i,true,false));
                if (incoming) {
                    // Threat incoming
                    blocks.add(new VariableDirectionSortedGhostIncomingBlock(i,false,false));
                    // Edible incoming
                    blocks.add(new VariableDirectionSortedGhostIncomingBlock(i,true,false));
                }
                if(Parameters.parameters.booleanParameter("trapped")) {
                    // Threat trapped
                    blocks.add(new VariableDirectionSortedGhostTrappedBlock(i,false,false));
                    // Edible trapped
                    blocks.add(new VariableDirectionSortedGhostTrappedBlock(i,true,false));
                }
            }
        }
        // Look ahead
        blocks.add(new VariableDirectionKStepPillCountBlock(direction));
        blocks.add(new VariableDirectionKStepJunctionCountBlock(direction));
        // Counts
        blocks.add(new PowerPillsRemainingBlock(true, false));
        blocks.add(new PillsRemainingBlock(true, false));
        // For limited edible time
        if(!infiniteEdibleTime) {
            blocks.add(new CountEdibleGhostsBlock(true, false));
            blocks.add(new EdibleTimesBlock());
        }
        // Other
        blocks.add(new AnyEdibleGhostBlock());
        blocks.add(new AllThreatsPresentBlock());
        blocks.add(new IsCloseToPowerPill());
        // High level
        //blocks.add(new VariableDirectionDistanceFromJunctionToGhostBlock(direction, true, true));
        //blocks.add(new VariableDirectionCountAllPillsInKStepsBlock(direction));
        if (Parameters.parameters.booleanParameter("highLevel")) {
            blocks.add(new VariableDirectionCountJunctionOptionsBlock());
            // These sensors don't seem to help
//            blocks.add(new VariableDirectionPowerPillBlocksJunctionBlock(direction));
//            blocks.add(new VariableDirectionGhostBlocksJunctionBlock(direction));
        }
        //blocks.add(new VariableDirectionOneStepSafeBlock());
    }
}
