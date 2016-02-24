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
public class GhostVariableDirectionPowerPillDistanceBlock extends GhostVariableDirectionDistanceBlock {

    public GhostVariableDirectionPowerPillDistanceBlock(int exclude){
        super(exclude);
    }
    
    @Override
    public String getType() {
        return "Power Pill";
    }

    @Override
    public int[] getTargets(GameFacade gf, int ghostIndex) {
        return gf.getActivePowerPillsIndices();
    }
}
