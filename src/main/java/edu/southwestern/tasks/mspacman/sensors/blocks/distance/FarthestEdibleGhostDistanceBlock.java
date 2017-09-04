package edu.southwestern.tasks.mspacman.sensors.blocks.distance;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class FarthestEdibleGhostDistanceBlock extends FarthestDistanceBlock {

	@Override
	public int[] getTargets(GameFacade gf) {
		int[] temp = gf.getEdibleGhostLocations();
		return temp.length == CommonConstants.numActiveGhosts ? temp : new int[0];
	}

	@Override
	public String getType() {
		return "Edible Ghost";
	}
}
