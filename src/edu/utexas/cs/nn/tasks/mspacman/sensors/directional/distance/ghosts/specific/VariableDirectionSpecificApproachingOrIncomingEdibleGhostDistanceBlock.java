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
public class VariableDirectionSpecificApproachingOrIncomingEdibleGhostDistanceBlock extends VariableDirectionDistanceBlock {
    private final int ghostIndex;

    public VariableDirectionSpecificApproachingOrIncomingEdibleGhostDistanceBlock(int dir, int ghostIndex) {
        super(dir);
        this.ghostIndex = ghostIndex;
    }

    @Override
    public String getType() {
        return "Approaching/Incoming Edible Ghost " + ghostIndex;
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        if(gf.isGhostEdible(ghostIndex) && (gf.ghostApproachingPacman(ghostIndex) || gf.isGhostIncoming(dir, ghostIndex))){
            return new int[]{gf.getGhostCurrentNodeIndex(ghostIndex)};
        } else {
            return new int[0];
        }
    }
}
