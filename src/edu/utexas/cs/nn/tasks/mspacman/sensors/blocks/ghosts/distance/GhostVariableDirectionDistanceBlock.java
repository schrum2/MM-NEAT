/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.ghosts.distance;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.ghosts.GhostVariableDirectionBlock;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public abstract class GhostVariableDirectionDistanceBlock extends GhostVariableDirectionBlock {

    public static ArrayList<Integer> excludedNodes = new ArrayList<Integer>();
    public final int numberToExclude;

    public GhostVariableDirectionDistanceBlock(int exclude){
        this.numberToExclude = exclude;
    }
    
    @Override
    public String getLabel() {
        return "Distance to " + numberToExclude + " Nearest " + getType();
    }

    @Override
    public double getValue(GameFacade gf, int ghostIndex) {
        if(numberToExclude == 0) {
            excludedNodes.clear();
        }
        assert numberToExclude == excludedNodes.size() : "Not excluding the right number of node results: " + numberToExclude + ":" + excludedNodes;
        final int current = gf.getGhostCurrentNodeIndex(ghostIndex);
        final int[] targets = ArrayUtil.setDifference(getTargets(gf,ghostIndex), excludedNodes);
        if (targets.length == 0) {
            return 1.0; // Distance is "infinity"   
        } else {
            Pair<Integer, int[]> pair = gf.getTargetInDir(current, targets, direction);
            excludedNodes.add(pair.t1); // Exclude this result from the next call
            int[] path = pair.t2;
            double distance = path.length;
            double result = (Math.min(distance, GameFacade.MAX_DISTANCE) / GameFacade.MAX_DISTANCE);
            return result;
        }
    }

    public abstract String getType();

    public abstract int[] getTargets(GameFacade gf,int ghostIndex);

}
