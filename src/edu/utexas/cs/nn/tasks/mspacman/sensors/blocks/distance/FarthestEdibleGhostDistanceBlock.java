package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class FarthestEdibleGhostDistanceBlock extends FarthestDistanceBlock {

    @Override
    public int[] getTargets(GameFacade gf) {
        int[] temp = gf.getEdibleGhostLocations();
        return temp.length == CommonConstants.numActiveGhosts ? temp : new int[0];
    }

    @Override
    public String getType() {
        return "Edible Ghost";
    }
}
