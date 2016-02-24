/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.blocking;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionPowerPillBlocksThreatGhostBlock extends VariableDirectionItemBlocksTargetBlock {

    public VariableDirectionPowerPillBlocksThreatGhostBlock(int dir) {
        super(0, dir); // If there are no threat ghosts, then the path is safe
    }

    public VariableDirectionPowerPillBlocksThreatGhostBlock() {
        this(-1); // If there are no threat ghosts, then the path is safe
    }

    @Override
    public int[] getObstacles(GameFacade gf) {
        return gf.getActivePowerPillsIndices();
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getThreatGhostLocations();
    }

    @Override
    public String getObstacleType() {
        return "Power Pill";
    }

    @Override
    public String getTargetType() {
        return "Threat Ghost";
    }
}
