/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionKStepThreatGhostCountBlock extends VariableDirectionKStepCountBlock {

    private final boolean[] includeGhosts;

    public VariableDirectionKStepThreatGhostCountBlock(int dir, boolean max) {
        this(dir, new boolean[]{true, true, true, true}, max);
    }

    public VariableDirectionKStepThreatGhostCountBlock(int dir) {
        this(dir, new boolean[]{true, true, true, true}, true);
    }

    public VariableDirectionKStepThreatGhostCountBlock(int dir, boolean[] include) {
        this(dir, include, true);
    }

    public VariableDirectionKStepThreatGhostCountBlock(int dir, boolean[] include, boolean max) {
        super(dir, max);
        this.includeGhosts = include;
    }

    @Override
    public int[] getCountTargets(GameFacade gf) {
        return gf.getThreatGhostLocations(includeGhosts);
    }

    @Override
    public String getType() {
        return "Threat Ghost " + Arrays.toString(includeGhosts);
    }

    @Override
    public int maxCount(GameFacade gf) {
        return gf.getNumActiveGhosts();
    }
}
