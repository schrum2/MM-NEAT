/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class AnyEdibleGhostBlock extends BooleanSensorBlock {

	@Override
	public String senseLabel() {
		return "Some Ghost Edible";
	}

	@Override
	public boolean predicate(GameFacade gf, int lastDirection) {
		return gf.anyIsEdible();
	}
}
