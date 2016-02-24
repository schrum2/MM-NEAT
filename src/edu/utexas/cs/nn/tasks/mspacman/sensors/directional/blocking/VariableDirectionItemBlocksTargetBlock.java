/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.blocking;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 *
 * @author Jacob Schrum
 */
public abstract class VariableDirectionItemBlocksTargetBlock extends VariableDirectionBlock {

    private final double noTargets;

    public VariableDirectionItemBlocksTargetBlock(double noTargets, int dir) {
        super(dir);
        this.noTargets = noTargets;
    }

    public VariableDirectionItemBlocksTargetBlock(double noTargets) {
        this(noTargets, -1);
    }

    public double wallValue() {
        return noTargets;
    }

    @Override
    public double getValue(GameFacade gf) {
        final int current = gf.getPacmanCurrentNodeIndex();
        final int[] targets = getTargets(gf);
        if (targets.length == 0) {
            return noTargets;
        } else {
            Pair<Integer, int[]> pair = gf.getTargetInDir(current, targets, dir);
            int[] path = pair.t2; // contains end target, but does not contain current
            final int[] obstacles = getObstacles(gf);
            int[] obstaclesOnPath = ArrayUtil.intersection(path, obstacles);
            return obstaclesOnPath.length == 0 ? 0 : 1;
        }
    }

    public abstract int[] getObstacles(GameFacade gf);

    public abstract int[] getTargets(GameFacade gf);

    @Override
    public String getLabel() {
        return getObstacleType() + " Before " + getTargetType();
    }

    public abstract String getObstacleType();

    public abstract String getTargetType();
}
