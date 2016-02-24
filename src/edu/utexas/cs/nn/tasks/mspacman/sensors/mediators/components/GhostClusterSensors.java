/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.components;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.cluster.GhostClusterBlock;

/**
 *
 * @author Jacob Schrum
 */
public class GhostClusterSensors extends BlockLoadedInputOutputMediator {

    public GhostClusterSensors(boolean senseEdibleGhosts) {
        super();
        if (CommonConstants.numActiveGhosts >= 4) {
            blocks.add(new GhostClusterBlock(true));
            if (senseEdibleGhosts) {
                blocks.add(new GhostClusterBlock(false));
            }
        }
        if (CommonConstants.numActiveGhosts >= 3) {
            blocks.add(new GhostClusterBlock(new boolean[]{true, true, true, false}, true));
            if (senseEdibleGhosts) {
                blocks.add(new GhostClusterBlock(new boolean[]{true, true, true, false}, false));
            }
            // Not needed if there are only two ghosts, since the "tree" will be a straight path
//            blocks.add(new GhostTreeClusterBlock(true, true)); // All threats present
//            if (senseEdibleGhosts) {
//                blocks.add(new GhostTreeClusterBlock(false, false)); // Whatever edible present
//            }
        }
        if (CommonConstants.numActiveGhosts >= 2) {
            blocks.add(new GhostClusterBlock(new boolean[]{true, true, false, false}, true));
            if (senseEdibleGhosts) {
                blocks.add(new GhostClusterBlock(new boolean[]{true, true, false, false}, false));
            }
        }
    }
}
