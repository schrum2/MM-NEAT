/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.ghosts.distance;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class GhostVariableDirectionMsPacManDistanceBlock extends GhostVariableDirectionDistanceBlock {

    public GhostVariableDirectionMsPacManDistanceBlock(){
        super(0);
    }
    
    @Override
    public String getType() {
        return "Ms. Pac-Man";
    }

    @Override
    public int[] getTargets(GameFacade gf, int ghostIndex) {
        return new int[]{gf.getPacmanCurrentNodeIndex()};
    }

}
