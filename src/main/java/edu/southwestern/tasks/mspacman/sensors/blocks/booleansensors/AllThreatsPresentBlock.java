/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob
 */
public class AllThreatsPresentBlock extends BooleanSensorBlock {

	@Override
	public String senseLabel() {
		return "All Threats Present";
	}

	@Override
	public boolean predicate(GameFacade gf, int lastDirection) {
		return gf.getThreatGhostLocations().length == CommonConstants.numActiveGhosts;
	}
}
