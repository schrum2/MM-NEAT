/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionPowerPillDistanceBlock extends VariableDirectionDistanceBlock {

    public VariableDirectionPowerPillDistanceBlock(int dir) {
        super(dir);
    }

    @Override
    public String getType() {
        return "Power Pill";
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getActivePowerPillsIndices();
    }
}
