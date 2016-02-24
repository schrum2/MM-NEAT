/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.veryclose;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class IsCloseToPowerPill extends IsCloseBlock {

    private static final int CLOSE_POWER_PILL_DISTANCE = 10;

    public IsCloseToPowerPill() {
        super(CLOSE_POWER_PILL_DISTANCE);
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getActivePowerPillsIndices();
    }

    @Override
    public String getType() {
        return "Power Pill";
    }
}
