/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.specific;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.VariableDirectionDistanceBlock;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionSpecificApproachingThreatGhostDistanceBlock extends VariableDirectionDistanceBlock {
    private final int ghostIndex;

    public VariableDirectionSpecificApproachingThreatGhostDistanceBlock(int dir, int ghostIndex) {
        super(dir);
        this.ghostIndex = ghostIndex;
    }

    @Override
    public String getType() {
        return "Approaching Threat Ghost " + ghostIndex;
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        if(gf.isGhostThreat(ghostIndex) && gf.ghostApproachingPacman(ghostIndex)){
            return new int[]{gf.getGhostCurrentNodeIndex(ghostIndex)};
        } else {
            return new int[0];
        }
    }
}
