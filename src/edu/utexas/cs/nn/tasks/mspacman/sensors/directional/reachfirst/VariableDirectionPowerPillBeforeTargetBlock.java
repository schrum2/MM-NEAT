/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.reachfirst;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 *
 * @author Jacob Schrum
 */
public abstract class VariableDirectionPowerPillBeforeTargetBlock extends VariableDirectionBlock {

    public VariableDirectionPowerPillBeforeTargetBlock(int dir) {
        super(dir);
    }

    @Override
    public double getValue(GameFacade gf) {
        int[] targets = getTargets(gf);
        if(targets.length == 0){
            return 0;
        }
        Pair<Integer, int[]> pair = gf.getTargetInDir(gf.getPacmanCurrentNodeIndex(), targets, dir);
        int[] pathToTarget = pair.t2;
        int[] powerPills = gf.getActivePowerPillsIndices();
        int[] powerPillsOnWayToTarget = ArrayUtil.intersection(pathToTarget, powerPills);
        int result = powerPillsOnWayToTarget.length > 0 ? 1 : 0;
//        if(CommonConstants.watch && result == 1){
//            gf.addPoints(Color.DARK_GRAY, powerPillsOnWayToTarget);
//        }
        return result;
    }

    @Override
    public String getLabel() {
        return "Power Pill On Path to " + getTargetType();
    }

    @Override
    public double wallValue() {
        return 0;
    }

    public abstract int[] getTargets(GameFacade gf);

    public abstract String getTargetType();
}
