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
public class VariableDirectionKStepApproachingOrIncomingEdibleGhostCountBlock extends VariableDirectionKStepCountBlock {
    private final boolean[] includeGhosts;

    public VariableDirectionKStepApproachingOrIncomingEdibleGhostCountBlock(int dir, boolean max) {
        this(dir, new boolean[]{true,true,true,true}, max);
    }    

    public VariableDirectionKStepApproachingOrIncomingEdibleGhostCountBlock(int dir, boolean[] include, boolean max) {
        super(dir, max);
        this.includeGhosts = include;
    }

    @Override
    public int[] getCountTargets(GameFacade gf) {
        return gf.getApproachingOrIncomingEdibleGhostLocations(dir, includeGhosts);
    }

    @Override
    public String getType() {
        return "Approaching/Incoming Edible Ghost";
    }

    @Override
    public int maxCount(GameFacade gf) {
        return gf.getNumActiveGhosts();
    }
}
