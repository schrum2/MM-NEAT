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
public class VariableDirectionPowerPillBlocksJunctionBlock extends VariableDirectionItemBlocksTargetBlock {

    public VariableDirectionPowerPillBlocksJunctionBlock(int dir) {
        super(0, dir);
    }

    public VariableDirectionPowerPillBlocksJunctionBlock() {
        this(-1); 
    }

    @Override
    public int[] getObstacles(GameFacade gf) {
        return gf.getActivePowerPillsIndices();
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getJunctionIndices();
    }

    @Override
    public String getObstacleType() {
        return "Power Pill";
    }

    @Override
    public String getTargetType() {
        return "Junction";
    }
}
