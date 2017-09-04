/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.blocks.proximity;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.util.datastructures.ArrayUtil;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class ThreatGhostRetreatProximityBlock extends RetreatProximityBlock {

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
