/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class FarthestThreatGhostDistanceBlock extends FarthestDistanceBlock {

    @Override
    public int[] getTargets(GameFacade gf) {
        int[] temp = gf.getThreatGhostLocations();
        return temp.length == CommonConstants.numActiveGhosts ? temp : new int[0];
    }

    @Override
    public String getType() {
        return "Threat Ghost";
    }
}
