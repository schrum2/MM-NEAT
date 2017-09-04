package edu.southwestern.tasks.mspacman.sensors.blocks.nearestfarthest;

import java.util.ArrayList;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.util.datastructures.ArrayUtil;

/**
 *
 * @author Jacob Schrum
 */
public class NearestFarthestThreatGhostBlock extends NearestFarthestIndexBlock {

	public NearestFarthestThreatGhostBlock(boolean nearest) {
		super(nearest);
	}

	public NearestFarthestThreatGhostBlock(boolean nearest, int absence) {
		super(nearest, absence);
	}

	@Override
	public int[] getTargets(GameFacade gf) {
		ArrayList<Integer> ghostPositions = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			if (gf.isGhostThreat(i)) {
				ghostPositions.add(gf.getGhostCurrentNodeIndex(i));
			} else if (!gf.isGhostEdible(i) && gf.getGhostLairTime(i) < GameFacade.DANGEROUS_TIME) {
				ghostPositions.add(gf.getGhostInitialNodeIndex());
			}
		}
		return ArrayUtil.intArrayFromArrayList(ghostPositions);
	}

	@Override
	public String typeOfTarget() {
		return "Threatening Ghost";
	}
}
