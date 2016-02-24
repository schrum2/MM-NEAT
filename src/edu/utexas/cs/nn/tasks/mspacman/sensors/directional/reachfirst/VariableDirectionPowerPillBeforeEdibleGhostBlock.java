/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.reachfirst;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionPowerPillBeforeEdibleGhostBlock extends VariableDirectionPowerPillBeforeTargetBlock {

    private final boolean[] includeGhosts;

    public VariableDirectionPowerPillBeforeEdibleGhostBlock(int dir) {
        super(dir);
        int numActive = Parameters.parameters.integerParameter("numActiveGhosts");
        includeGhosts = new boolean[numActive];
        for (int i = 0; i < numActive; i++) {
            includeGhosts[i] = true;
        }
    }

    public VariableDirectionPowerPillBeforeEdibleGhostBlock(int dir, boolean[] ghosts) {
        super(dir);
        this.includeGhosts = ghosts;
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getEdibleGhostLocations(includeGhosts);
    }

    @Override
    public String getTargetType() {
        return "Edible Ghost " + Arrays.toString(this.includeGhosts);
    }
}
