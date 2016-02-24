/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts.incoming;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.VariableDirectionDistanceBlock;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionApproachingThreatGhostDistanceBlock extends VariableDirectionDistanceBlock {

    public VariableDirectionApproachingThreatGhostDistanceBlock(int dir) {
        super(dir);
    }

    @Override
    public String getType() {
        return "Approaching Threat Ghost";
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getApproachingThreatGhostLocations();
    }
}
