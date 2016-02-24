/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.farthest;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionFarthestEdibleGhostDistanceBlock extends VariableDirectionFarthestDistanceBlock {

    public VariableDirectionFarthestEdibleGhostDistanceBlock(int dir) {
        super(dir);
    }

    @Override
    public String getType() {
        return "Edible Ghost";
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getEdibleGhostLocations();
    }
}
