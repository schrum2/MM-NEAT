/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class AverageEdibleGhostDistanceBlock extends AverageDistanceBlock {

    @Override
    public int[] getTargets(GameFacade gf) {
        int[] presentEdible = gf.getEdibleGhostLocations();
        int[] totalThreats = new int[CommonConstants.numActiveGhosts];
        Arrays.fill(totalThreats, -1);
        System.arraycopy(presentEdible, 0, totalThreats, 0, presentEdible.length);
        return totalThreats;
    }

    @Override
    public String getType() {
        return "Edible Ghost";
    }
}
