package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class NearestEdibleGhostDistanceBlock extends NearestDistanceBlock {

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getEdibleGhostLocations();
    }

    @Override
    public String getType() {
        return "Edible Ghost";
    }
}
