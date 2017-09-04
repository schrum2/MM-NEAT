/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.blocks.distance;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class FarthestThreatGhostDistanceBlock extends FarthestDistanceBlock {

	@Override
	public int[] getTargets(GameFacade gf) {
		int[] temp = gf.getThreatGhostLocations();
		return temp.length == CommonConstants.numActiveGhosts ? temp : new int[0];
	}

	@Override
	public String getType() {
		return "Threat Ghost";
	}
}
