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
public class VariableDirectionThreatGhostBlocksJunctionBlock extends VariableDirectionItemBlocksTargetBlock {

    public VariableDirectionThreatGhostBlocksJunctionBlock(int dir) {
        super(-1, dir); // irrelevant, because there are always junctions
    }

    public VariableDirectionThreatGhostBlocksJunctionBlock() {
        this(-1); // irrelevant, because there are always junctions
    }

    @Override
    public int[] getObstacles(GameFacade gf) {
        return gf.getThreatGhostLocations();
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getJunctionIndices();
    }

    @Override
    public String getObstacleType() {
        return "Threat Ghost";
    }

    @Override
    public String getTargetType() {
        return "Junction";
    }
}
