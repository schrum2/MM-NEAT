/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.proximity;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class ThreatGhostDirectionalProximityBlock extends DirectionalProximityBlock {

    @Override
    public int[] getTargets(GameFacade gf) {
        ArrayList<Integer> ghostPositions = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
        for (int i = 0; i < gf.getNumActiveGhosts(); i++) {
            if (gf.isGhostThreat(i)) {
                ghostPositions.add(gf.getGhostCurrentNodeIndex(i));
            } else if (!gf.isGhostEdible(i) && gf.getGhostLairTime(i) < GameFacade.DANGEROUS_TIME) {
                ghostPositions.add(gf.getGhostInitialNodeIndex());
            }
        }
        return ArrayUtil.intArrayFromArrayList(ghostPositions);
    }

    @Override
    public String targetType() {
        return "Threat Ghost";
    }
}
