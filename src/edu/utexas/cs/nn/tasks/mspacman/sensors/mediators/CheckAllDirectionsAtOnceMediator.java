/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators;

import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionEdibleGhostDistanceBlock;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.blocking.VariableDirectionPowerPillBlocksThreatGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.blocking.VariableDirectionThreatGhostBlocksJunctionBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts.*;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.*;

/**
 * Based on Brandstetter's CIG 2012 paper
 *
 * @author Jacob Schrum
 */
public class CheckAllDirectionsAtOnceMediator extends BlockLoadedInputOutputMediator {

    private int blocksPerDirection;

    public CheckAllDirectionsAtOnceMediator() {
        for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
            blocksPerDirection = 0; // redundantly calculated, but only at construction
            // Distances
            blocks.add(new VariableDirectionPillDistanceBlock(i));
            blocksPerDirection++;
            blocks.add(new VariableDirectionPowerPillDistanceBlock(i));
            blocksPerDirection++;
            blocks.add(new VariableDirectionEdibleGhostDistanceBlock(i));
            blocksPerDirection++;
            blocks.add(new VariableDirectionThreatGhostDistanceBlock(i));
            blocksPerDirection++;
            blocks.add(new VariableDirectionJunctionDistanceBlock(i));
            blocksPerDirection++;
            // Obstructions
            blocks.add(new VariableDirectionThreatGhostBlocksJunctionBlock(i));
            blocksPerDirection++;
            blocks.add(new VariableDirectionPowerPillBlocksThreatGhostBlock(i));
            blocksPerDirection++;
            // Look ahead
            blocks.add(new VariableDirectionKStepPillCountBlock(i));
            blocksPerDirection++;
            blocks.add(new VariableDirectionKStepPowerPillCountBlock(i));
            blocksPerDirection++;
            blocks.add(new VariableDirectionKStepEdibleGhostCountBlock(i, true));
            blocksPerDirection++;
            blocks.add(new VariableDirectionKStepThreatGhostCountBlock(i));
            blocksPerDirection++;
            blocks.add(new VariableDirectionKStepJunctionCountBlock(i));
            blocksPerDirection++;
        }
    }

    @Override
    public double[] getInputs(GameFacade gs, int currentDir) {
        // May need to change the absolute directions that the sensor blocks look at
        // to match the relative directions
        if (CommonConstants.relativePacmanDirections) {
            for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
                for (int j = 0; j < blocksPerDirection; j++) {
                    ((VariableDirectionBlock) blocks.get((i * blocksPerDirection) + j)).setDirection((currentDir + i) % GameFacade.NUM_DIRS);
                }
            }
        }
        return super.getInputs(gs, currentDir);
    }
}
