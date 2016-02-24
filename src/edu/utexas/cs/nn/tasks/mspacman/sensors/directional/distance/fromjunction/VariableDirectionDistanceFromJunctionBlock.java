/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.fromjunction;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 *
 * @author Jacob Schrum
 */
public abstract class VariableDirectionDistanceFromJunctionBlock extends VariableDirectionBlock {

    public VariableDirectionDistanceFromJunctionBlock(int dir) {
        super(dir);
    }

    public double getValue(GameFacade gf) {
        final int current = gf.getPacmanCurrentNodeIndex();
        final int[] targets = getTargets(gf);
        if (targets.length == 0) {
            return 1.0; // Distance is "infinity"   
        } else {
            Pair<Integer, int[]> junctionPair = gf.getTargetInDir(current, gf.getJunctionIndices(), dir);
            Pair<Integer, int[]> ghostPair = gf.getTargetInDir(current, targets, dir);            
            double distance;
            if(ghostPair.t2.length >= junctionPair.t2.length){
                distance = ghostPair.t2.length - junctionPair.t2.length;
            } else {
                distance = 0;
            }
            return (Math.min(distance, GameFacade.MAX_DISTANCE) / GameFacade.MAX_DISTANCE);
        }
    }

    public double wallValue() {
        return 1;
    }

    @Override
    public String getLabel() {
        return "Distance from Junction to " + getType();
    }

    public abstract String getType();

    public abstract int[] getTargets(GameFacade gf);
}
