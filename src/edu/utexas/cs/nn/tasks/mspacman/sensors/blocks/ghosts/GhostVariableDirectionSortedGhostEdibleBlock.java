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
public class GhostVariableDirectionSortedGhostEdibleBlock extends GhostVariableDirectionBlock {

    private final int order;

    public GhostVariableDirectionSortedGhostEdibleBlock(int order) {
        super();
        this.order = order;
    }

    @Override
    public double getValue(GameFacade gf, int ghostIndex) {
        ArrayList<Integer> ghosts = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
        int thisGhost = gf.getGhostCurrentNodeIndex(ghostIndex);
        ghosts.remove(new Integer(thisGhost));
        for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
            if (!gf.ghostInLair(i)) {
                ghosts.add(i);
            }
        }
        if (order >= ghosts.size()) {
            return 0; // Not incoming if in lair
        }
        Collections.sort(ghosts, CommonConstants.checkEachAbsoluteDistanceGhostSort
                ? new GhostComparator(gf, true, true)
                : new DirectionalGhostComparator(gf, true, true, direction));
        return gf.isGhostEdible(ghosts.get(order)) ? 1 : 0;
    }

    @Override
    public String getLabel() {
        return order + " Closest Ghost Edible";
    }
}
