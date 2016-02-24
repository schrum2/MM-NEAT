/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.ghosts.distance;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.ghosts.DirectionalGhostComparator;
import edu.utexas.cs.nn.tasks.mspacman.ghosts.GhostComparator;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Jacob Schrum
 */
public class GhostVariableDirectionSortedGhostDistanceBlock extends GhostVariableDirectionDistanceBlock{

    private final int order;
    private final boolean edibleClose;
    private final boolean proximityOnly;

    public GhostVariableDirectionSortedGhostDistanceBlock(int order){
        this(order, true, true);
    }

    public GhostVariableDirectionSortedGhostDistanceBlock(int order, boolean edibleClose, boolean proximityOnly) {
        super(0);
        this.order = order;
        this.edibleClose = edibleClose;
        this.proximityOnly = proximityOnly;
    }
    
    @Override
    public String getType() {
        return order + " Closest " + (proximityOnly ? "" : (edibleClose ? "Edible " : "Threat ")) + "Ghost";
    }

    @Override
    public int[] getTargets(GameFacade gf, int ghostIndex) {
        ArrayList<Integer> ghosts = gf.getGhostIndices(edibleClose, proximityOnly);
        int thisGhost = gf.getGhostCurrentNodeIndex(ghostIndex);
        ghosts.remove(new Integer(thisGhost));
        if (order >= ghosts.size()) {
            return new int[0]; // Target in lair will result in distance of infinity
        }
        Collections.sort(ghosts, CommonConstants.checkEachAbsoluteDistanceGhostSort
                ? new GhostComparator(gf, edibleClose, proximityOnly)
                : new DirectionalGhostComparator(gf, edibleClose, proximityOnly, this.direction));
        return new int[]{gf.getGhostCurrentNodeIndex(ghosts.get(order))};
    }

}
