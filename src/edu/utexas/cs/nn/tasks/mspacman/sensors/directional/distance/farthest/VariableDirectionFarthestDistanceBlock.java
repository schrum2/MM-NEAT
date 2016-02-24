/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.farthest;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 *
 * @author Jacob Schrum
 */
public abstract class VariableDirectionFarthestDistanceBlock extends VariableDirectionBlock {

    public VariableDirectionFarthestDistanceBlock(int dir) {
        super(dir);
    }

    public double getValue(GameFacade gf) {
        final int current = gf.getPacmanCurrentNodeIndex();
        final int[] targets = getTargets(gf);
        if (targets.length == 0) {
            return 1.0; // Distance is "infinity"   
        } else {
            double maxDistance = 0;
            for(Integer target : targets) {
                int[] path = gf.getPathInDirFromNew(current, target, dir);
                double distance = path.length;
                maxDistance = Math.max(maxDistance, distance);
            }
            return (Math.min(maxDistance, GameFacade.MAX_DISTANCE) / GameFacade.MAX_DISTANCE);
        }
    }

    public double wallValue() {
        return 1;
    }

    @Override
    public String getLabel() {
        return "Distance to Farthest " + getType();
    }

    public abstract String getType();

    public abstract int[] getTargets(GameFacade gf);
}
