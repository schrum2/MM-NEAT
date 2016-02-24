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
public class VariableDirectionThreatGhostDistanceBlock extends VariableDirectionDistanceBlock {

    public VariableDirectionThreatGhostDistanceBlock(int dir) {
        super(dir);
    }

    @Override
    public String getType() {
        return "Threat Ghost";
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getThreatGhostLocations();
    }
}
