/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.ghosts;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.ghosts.DirectionalGhostComparator;
import edu.utexas.cs.nn.tasks.mspacman.ghosts.GhostComparator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.VariableDirectionDistanceBlock;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionSortedGhostDistanceBlock extends VariableDirectionDistanceBlock {

    private final int order;
    private final boolean edibleClose;
    private final boolean proximityOnly;

    public VariableDirectionSortedGhostDistanceBlock(int order) {
        this(-1, order, true, true);
    }

    public VariableDirectionSortedGhostDistanceBlock(int dir, int order, boolean edibleClose, boolean proximityOnly) {
        super(dir);
        this.order = order;
        this.edibleClose = edibleClose;
        this.proximityOnly = proximityOnly;
    }

    @Override
    public String getType() {
        return order + " Closest " + (proximityOnly ? "" : (edibleClose ? "Edible " : "Threat ")) + "Ghost";
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        ArrayList<Integer> ghosts = gf.getGhostIndices(edibleClose, proximityOnly);
        if (order >= ghosts.size()) {
            return new int[0]; // Target in lair will result in distance of infinity
        }
        Collections.sort(ghosts, CommonConstants.checkEachAbsoluteDistanceGhostSort
                ? new GhostComparator(gf, edibleClose, proximityOnly)
                : new DirectionalGhostComparator(gf, edibleClose, proximityOnly, dir));
        //System.out.println("Time:"+gf.getTotalTime()+":dir:"+dir+":Order:"+order+":ghost:"+ghosts.get(order));
        return new int[]{gf.getGhostCurrentNodeIndex(ghosts.get(order))};
    }
}
