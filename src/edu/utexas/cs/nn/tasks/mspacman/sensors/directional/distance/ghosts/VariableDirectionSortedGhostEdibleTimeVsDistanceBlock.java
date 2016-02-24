/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts;

import edu.utexas.cs.nn.networks.ActivationFunctions;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.ghosts.DirectionalGhostComparator;
import edu.utexas.cs.nn.tasks.mspacman.ghosts.GhostComparator;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import java.util.ArrayList;
import java.util.Collections;
import pacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionSortedGhostEdibleTimeVsDistanceBlock extends VariableDirectionSortedGhostDistanceBlock {

    private final int order;
    private int ghostIndex; // used to pass info between functions, like an extra return

    public VariableDirectionSortedGhostEdibleTimeVsDistanceBlock(int order) {
        this(-1, order);
    }

    public VariableDirectionSortedGhostEdibleTimeVsDistanceBlock(int dir, int order) {
        super(dir);
        this.order = order;
    }

    @Override
    public String getType() {
        return order + " Closest Ghost";
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        ArrayList<Integer> ghosts = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
        for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
            if (!gf.ghostInLair(i)) {
                ghosts.add(i);
            }
        }
        if (order >= ghosts.size()) {
            return new int[0]; // Target in lair will result in distance of infinity
        }
        Collections.sort(ghosts, CommonConstants.checkEachAbsoluteDistanceGhostSort
                ? new GhostComparator(gf, true, true)
                : new DirectionalGhostComparator(gf, true, true, dir));
        //System.out.println("Time:"+gf.getTotalTime()+":dir:"+dir+":Order:"+order+":ghost:"+ghosts.get(order));
        ghostIndex = ghosts.get(order);
        return new int[]{gf.getGhostCurrentNodeIndex(ghostIndex)};
    }

    @Override
    public double getValue(GameFacade gf) {
        if(numberToExclude == 0) {
            excludedNodes.clear();
        }
        assert numberToExclude == excludedNodes.size() : "Not excluding the right number of node results: " + numberToExclude + ":" + excludedNodes;
        final int current = gf.getPacmanCurrentNodeIndex();
        final int[] targets = ArrayUtil.setDifference(getTargets(gf), excludedNodes);
        if (targets.length == 0) {
            //excludedNodes.add(-1); // non-existant node
            return -1.0; // Distance is "infinity"   
        } else {
            Pair<Integer, int[]> pair = gf.getTargetInDir(current, targets, dir);
            excludedNodes.add(pair.t1); // Exclude this result from the next call
            int[] path = pair.t2;
            int edibleTime = gf.getGhostEdibleTime(ghostIndex);
            double distance = path.length;
            double result = ((edibleTime - distance) / Constants.EDIBLE_TIME);
            result = ActivationFunctions.fullLinear(result);
            //System.out.println("Distance:"+distance+":result:"+result);
            return result;
        }
    }

    @Override
    public double wallValue() {
        return -1;
    }

    @Override
    public String getLabel() {
        return "Edible Time Vs Distance to " + numberToExclude + " Nearest " + getType();
    }    
}
