/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.lookahead;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob
 */
public class DirectionalPillsStaticLookAheadBlock extends DirectionalStaticLookAheadBlock {

    @Override
    public String targetType() {
        return "Pills";
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getActivePillsIndices();
    }

    @Override
    public double maxCount(GameFacade gf) {
        return gf.getNumActivePills();
    }
}
