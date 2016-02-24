/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.approaching;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class ApproachingPowerPillBlock extends ApproachingLocationBlock {

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getActivePowerPillsIndices();
    }

    @Override
    public String typeOfTarget() {
        return "Power Pill";
    }
}
