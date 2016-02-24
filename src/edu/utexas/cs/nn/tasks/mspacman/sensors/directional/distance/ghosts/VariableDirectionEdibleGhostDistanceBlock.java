/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.VariableDirectionDistanceBlock;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionEdibleGhostDistanceBlock extends VariableDirectionDistanceBlock {

    public VariableDirectionEdibleGhostDistanceBlock(int dir) {
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
