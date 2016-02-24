/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.ghosts;

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
public class GhostVariableDirectionSortedGhostTrappedBlock extends GhostVariableDirectionBlock {

    private final int order;
    private final boolean edibleClose;
    private final boolean proximityOnly;
    
    public GhostVariableDirectionSortedGhostTrappedBlock(int order) {
        this(order, true, true);
    }
    
    public GhostVariableDirectionSortedGhostTrappedBlock(int order, boolean edibleClose, boolean proximityOnly) {
        super();
        this.order = order;
        this.edibleClose = edibleClose;
        this.proximityOnly = proximityOnly;
    }
    
    @Override
    public double getValue(GameFacade gf, int ghostIndex) {
        ArrayList<Integer> ghosts = gf.getGhostIndices(edibleClose, proximityOnly);
        int thisGhost = gf.getGhostCurrentNodeIndex(ghostIndex);
        ghosts.remove(new Integer(thisGhost));
        if (order >= ghosts.size()) {
            return 0; // Not incoming if in lair
        }
        Collections.sort(ghosts, CommonConstants.checkEachAbsoluteDistanceGhostSort
                ? new GhostComparator(gf, edibleClose, proximityOnly)
                : new DirectionalGhostComparator(gf, edibleClose, proximityOnly, this.direction));
        return gf.isGhostTrapped(direction, ghosts.get(order)) ? 1 : 0;
    }
    
    @Override
    public String getLabel() {
        return order + " Closest Ghost Trapped";
    }

}
