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
public class JunctionDirectionalProximityBlock extends DirectionalProximityBlock {

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getJunctionIndices();
    }

    @Override
    public String targetType() {
        return "Junction";
    }
}
