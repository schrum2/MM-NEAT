/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class NearestThreatGhostDistanceBlock extends NearestDistanceBlock {

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getThreatGhostLocations();
    }

    @Override
    public String getType() {
        return "Threat Ghost";
    }
}
