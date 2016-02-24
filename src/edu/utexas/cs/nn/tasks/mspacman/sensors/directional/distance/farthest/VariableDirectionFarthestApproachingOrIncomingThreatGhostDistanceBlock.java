/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.farthest;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionFarthestApproachingOrIncomingThreatGhostDistanceBlock extends VariableDirectionFarthestDistanceBlock {

    public VariableDirectionFarthestApproachingOrIncomingThreatGhostDistanceBlock(int dir) {
        super(dir);
    }

    @Override
    public String getType() {
        return "Approaching/Incoming Threat Ghost";
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getApproachingOrIncomingThreatGhostLocations(dir);
    }
}
