/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators;

import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.fromjunction.VariableDirectionDistanceFromJunctionToIncomingGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.fromjunction.VariableDirectionDistanceFromJunctionToGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionEdibleGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.farthest.VariableDirectionFarthestThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.farthest.VariableDirectionFarthestEdibleGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.farthest.VariableDirectionFarthestIncomingEdibleGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.farthest.VariableDirectionFarthestIncomingThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.incoming.VariableDirectionIncomingEdibleGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.incoming.VariableDirectionIncomingThreatGhostDistanceBlock;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting.CountEdibleGhostsBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting.CountThreatGhostsBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting.PillsRemainingBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting.PowerPillsRemainingBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.FarthestThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.NearestThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.time.EdibleTimesBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionLastActivationBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionLastDirectionBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionPillsBeforeJunctionBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts.*;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.*;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.reachfirst.*;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.scent.VariableDirectionKStepDeathScentBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.scent.VariableDirectionPersonalScentBlock;

/**
 * Based on Brandstetter's CIG 2012 paper, but replaced the "blocked" sensors
 * with "distance difference" sensors, and made some other changes.
 *
 * @author Jacob Schrum
 */
public class BestCheckEachDirectionMediator extends VariableDirectionBlockLoadedInputOutputMediator {

    public BestCheckEachDirectionMediator(){
        this(-1); // direction irrelevant
    }
    
    public BestCheckEachDirectionMediator(int direction) {
        boolean incoming = Parameters.parameters.booleanParameter("incoming");
        boolean communalDeathMemory = Parameters.parameters.booleanParameter("communalDeathMemory");
        boolean farthestDistances = Parameters.parameters.booleanParameter("farthestDis");
        
        blocks.add(new BiasBlock());
        // Distances
        blocks.add(new VariableDirectionPillDistanceBlock(direction));
        blocks.add(new VariableDirectionPowerPillDistanceBlock(direction));
        blocks.add(new VariableDirectionEdibleGhostDistanceBlock(direction));
        if(farthestDistances) {
            blocks.add(new VariableDirectionFarthestEdibleGhostDistanceBlock(direction));
        }
        blocks.add(new VariableDirectionThreatGhostDistanceBlock(direction));
        if(farthestDistances) {
            blocks.add(new VariableDirectionFarthestThreatGhostDistanceBlock(direction));
        }
        blocks.add(new VariableDirectionJunctionDistanceBlock(direction));
        blocks.add(new VariableDirectionDistanceFromJunctionToGhostBlock(direction, true,false));
        blocks.add(new VariableDirectionDistanceFromJunctionToGhostBlock(direction, false,true));
        if (incoming) {
            blocks.add(new VariableDirectionIncomingEdibleGhostDistanceBlock(direction));
            blocks.add(new VariableDirectionIncomingThreatGhostDistanceBlock(direction));
            blocks.add(new VariableDirectionDistanceFromJunctionToIncomingGhostBlock(direction, true));
            blocks.add(new VariableDirectionDistanceFromJunctionToIncomingGhostBlock(direction, false));
            if(farthestDistances) {
                blocks.add(new VariableDirectionFarthestIncomingEdibleGhostDistanceBlock(direction));
                blocks.add(new VariableDirectionFarthestIncomingThreatGhostDistanceBlock(direction));
            }
        }
        // Obstructions
        blocks.add(new VariableDirectionCloserToJunctionThanThreatGhostBlock(direction));
        blocks.add(new VariableDirectionCloserToDepthTwoJunctionThanThreatGhostBlock(direction));
        blocks.add(new VariableDirectionCloserToPowerPillThanThreatGhostBlock(direction));
        //blocks.add(new VariableDirectionCloserToEdibleGhostThanThreatGhostBlock(direction)); // Does not account for edible ghost movement
        blocks.add(new VariableDirectionPowerPillBeforeEdibleGhostBlock(direction));
        blocks.add(new VariableDirectionPowerPillBeforeJunctionBlock(direction));
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
        if (communalDeathMemory){
            blocks.add(new VariableDirectionKStepDeathScentBlock(direction, true));
            blocks.add(new VariableDirectionKStepDeathScentBlock(direction, false));
        }
        blocks.add(new VariableDirectionCountAllPillsInKStepsBlock(direction));
        // Misc
        blocks.add(new EdibleTimesBlock(new boolean[]{false, false, false, true}));
        blocks.add(new VariableDirectionPillsBeforeJunctionBlock(direction));
        blocks.add(new NearestThreatGhostDistanceBlock());
        if(farthestDistances) {
            blocks.add(new FarthestThreatGhostDistanceBlock());
        }
        blocks.add(new CountEdibleGhostsBlock(true, false));
        blocks.add(new CountThreatGhostsBlock(true, false));
        blocks.add(new PillsRemainingBlock(true, false));
        blocks.add(new PowerPillsRemainingBlock(true, false));
        // Tracking own behavior
        if(Parameters.parameters.booleanParameter("previousPreferences")){
            blocks.add(new VariableDirectionLastDirectionBlock(direction));
            for(int i = 0; i < GameFacade.NUM_DIRS; i++) {
                blocks.add(new VariableDirectionLastActivationBlock(direction, i));
            }
        }
        if(Parameters.parameters.booleanParameter("personalScent")){
            blocks.add(new VariableDirectionPersonalScentBlock(direction));
        }
    }
}
