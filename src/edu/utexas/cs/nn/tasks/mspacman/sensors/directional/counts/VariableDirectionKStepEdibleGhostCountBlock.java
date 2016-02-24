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
public class VariableDirectionKStepEdibleGhostCountBlock extends VariableDirectionKStepCountBlock {

    private final boolean[] includeGhosts;

    public VariableDirectionKStepEdibleGhostCountBlock(int dir, boolean max) {
        this(dir, new boolean[]{true, true, true, true}, max);
    }

    public VariableDirectionKStepEdibleGhostCountBlock(int dir, boolean[] include, boolean max) {
        super(dir, max);
        this.includeGhosts = include;
    }

    @Override
    public int[] getCountTargets(GameFacade gf) {
        return gf.getEdibleGhostLocations(includeGhosts);
    }

    @Override
    public String getType() {
        return "Edible Ghosts " + Arrays.toString(includeGhosts);
    }

    @Override
    public int maxCount(GameFacade gf) {
        return gf.getNumActiveGhosts();
    }
}
