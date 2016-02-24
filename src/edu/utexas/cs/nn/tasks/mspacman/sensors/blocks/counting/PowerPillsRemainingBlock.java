/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class PowerPillsRemainingBlock extends TargetPortionRemainingBlock {

    public PowerPillsRemainingBlock(boolean portion, boolean inverse) {
        super(portion, inverse);
    }

    @Override
    public int getTargetMax(GameFacade gf) {
        return gf.getNumberOfPowerPills();
    }

    @Override
    public int getTargetCurrent(GameFacade gf) {
        return gf.getActivePowerPillsIndices().length;
    }

    @Override
    public String getTargetType() {
        return "Power Pill";
    }
}
