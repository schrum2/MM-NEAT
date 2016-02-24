/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.ensemble;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.PowerPillAvoidanceBlock;
import java.awt.Color;

/**
 *
 * @author Jacob Schrum
 */
public class GhostsPillsOverlappingArbitrator extends OverlappingArbitrator {

    private final int crowdedDistance;

    public GhostsPillsOverlappingArbitrator() {
        this.crowdedDistance = Parameters.parameters.integerParameter("crowdedGhostDistance");
    }

    @Override
    public int modes() {
        return 2;
    }

    @Override
    public String[] modeLabels() {
        return new String[]{"Ghost Eating", "Pill Eating"};
    }

    @Override
    public boolean[] modesToConsider(GameFacade gf) {
        if (gf.anyIsEdible()) {
            // Even when ghosts are edible, it may be a good pill eating opportunity
            if (CommonConstants.watch) {
                gf.addPoints(Color.GREEN, gf.getEdibleGhostLocations());
            }
            return new boolean[]{true, true};
        } else if (gf.getNumActivePowerPills() == 0) {
            // No edible ghosts or power pills, so just eat pills
            if (CommonConstants.watch) {
                gf.addPoints(Color.GREEN, gf.getActivePillsIndices());
            }
            return new boolean[]{false, true};
        } else {
            int current = gf.getPacmanCurrentNodeIndex();
            int nearestPowerPill = gf.getClosestNodeIndexFromNodeIndex(current, gf.getActivePowerPillsIndices());
            if (gf.getShortestPathDistance(current, nearestPowerPill) < PowerPillAvoidanceBlock.CLOSE_DISTANCE) {
                // Think about eating ghosts when close to power pill
                if (CommonConstants.watch) {
                    gf.addPoints(Color.GREEN, gf.getShortestPath(current, nearestPowerPill));
                }
                return new boolean[]{true, false};
            }
            if(gf.getThreatGhostLocations().length > 0) {
                int nearestThreat = gf.getClosestNodeIndexFromNodeIndex(current, gf.getThreatGhostLocations());
                if (gf.getShortestPathDistance(current, nearestThreat) < crowdedDistance) {
                    // Think about ghosts and pills if a threat ghost is close
                    if (CommonConstants.watch) {
                        gf.addPoints(Color.GREEN, gf.getShortestPath(current, nearestThreat));
                    }
                    return new boolean[]{true, true};
                }
            }
        }
        // If threat is far away, then only consider pills
        if (CommonConstants.watch) {
            gf.addPoints(Color.GREEN, gf.getActivePillsIndices());
        }
        return new boolean[]{false, true};
    }
}
