/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.proximity;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class PillDirectionalProximityBlock extends DirectionalProximityBlock {

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getActivePillsIndices();
    }

    @Override
    public String targetType() {
        return "Pill";
    }
}
