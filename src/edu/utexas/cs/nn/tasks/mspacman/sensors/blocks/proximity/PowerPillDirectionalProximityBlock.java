package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.proximity;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class PowerPillDirectionalProximityBlock extends DirectionalProximityBlock {

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getActivePowerPillsIndices();
    }

    @Override
    public String targetType() {
        return "Power Pill";
    }
}
