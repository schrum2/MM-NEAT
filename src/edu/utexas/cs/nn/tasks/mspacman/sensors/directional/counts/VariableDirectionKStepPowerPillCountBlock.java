/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionKStepPowerPillCountBlock extends VariableDirectionKStepCountBlock {

    public VariableDirectionKStepPowerPillCountBlock(int dir) {
        super(dir);
    }

    @Override
    public int[] getCountTargets(GameFacade gf) {
        return gf.getActivePowerPillsIndices();
    }

    @Override
    public String getType() {
        return "Power Pill";
    }

    @Override
    public int maxCount(GameFacade gf) {
        return gf.getNumActivePowerPills();
    }
}
