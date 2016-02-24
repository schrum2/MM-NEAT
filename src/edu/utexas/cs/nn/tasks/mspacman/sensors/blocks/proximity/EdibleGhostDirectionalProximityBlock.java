package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.proximity;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class EdibleGhostDirectionalProximityBlock extends DirectionalProximityBlock {

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getEdibleGhostLocations();
    }

    @Override
    public String targetType() {
        return "Edible Ghost";
    }
}
