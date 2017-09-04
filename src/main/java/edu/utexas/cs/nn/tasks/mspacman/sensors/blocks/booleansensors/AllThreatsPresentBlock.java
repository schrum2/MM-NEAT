/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

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
