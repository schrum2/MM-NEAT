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
public class VariableDirectionThreatGhostBlocksEdibleGhostBlock extends VariableDirectionItemBlocksTargetBlock {

    public VariableDirectionThreatGhostBlocksEdibleGhostBlock(int dir) {
        super(0, dir); 
    }

    public VariableDirectionThreatGhostBlocksEdibleGhostBlock() {
        this(-1); 
    }

    @Override
    public int[] getObstacles(GameFacade gf) {
        return gf.getThreatGhostLocations();
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getEdibleGhostLocations();
    }

    @Override
    public String getObstacleType() {
        return "Threat Ghost";
    }

    @Override
    public String getTargetType() {
        return "Edible Ghost";
    }
}
