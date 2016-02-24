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
public class IsCloseToThreatGhost extends IsCloseBlock {

    private static final int CLOSE_GHOST_DISTANCE = 10;

    public IsCloseToThreatGhost() {
        super(CLOSE_GHOST_DISTANCE);
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getThreatGhostLocations();
    }

    @Override
    public String getType() {
        return "Threat Ghost";
    }
}
