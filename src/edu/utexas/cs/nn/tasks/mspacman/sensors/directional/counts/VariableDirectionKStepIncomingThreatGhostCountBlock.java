/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionKStepIncomingThreatGhostCountBlock extends VariableDirectionKStepCountBlock {
    private final boolean[] includeGhosts;

    public VariableDirectionKStepIncomingThreatGhostCountBlock(int dir, boolean max) {
        this(dir,max,new boolean[]{true,true,true,true});
    }

    public VariableDirectionKStepIncomingThreatGhostCountBlock(int dir, boolean max, boolean[] include) {
        super(dir, max);
        this.includeGhosts = include;
    }

    @Override
    public int[] getCountTargets(GameFacade gf) {
        return gf.getIncomingThreatGhostLocations(dir,includeGhosts);
    }

    @Override
    public String getType() {
        return "Incoming Threat Ghost";
    }

    @Override
    public int maxCount(GameFacade gf) {
        return gf.getNumActiveGhosts();
    }
}
