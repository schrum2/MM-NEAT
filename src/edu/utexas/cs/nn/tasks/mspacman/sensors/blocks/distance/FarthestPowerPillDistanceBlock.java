package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class FarthestPowerPillDistanceBlock extends FarthestDistanceBlock {

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getActivePowerPillsIndices();
    }

    @Override
    public String getType() {
        return "Power Pill";
    }
}
