/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.reachfirst;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionCloserToPowerPillThanThreatGhostBlock extends VariableDirectionCloserToTargetThanThreatGhostBlock {

    public VariableDirectionCloserToPowerPillThanThreatGhostBlock(int dir) {
        super(dir);
    }

    public VariableDirectionCloserToPowerPillThanThreatGhostBlock(int dir, int[] ghosts) {
        super(dir, ghosts);
    }

    @Override
    public String getTargetType() {
        return "Power Pill";
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getActivePowerPillsIndices();
    }
}
