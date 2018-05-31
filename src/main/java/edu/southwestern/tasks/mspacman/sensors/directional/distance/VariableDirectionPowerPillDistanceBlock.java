/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.directional.distance;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.util.MiscUtil;

/**
 * handles PO conditions (TODO: test)
 * @author Jacob Schrum
 */
public class VariableDirectionPowerPillDistanceBlock extends VariableDirectionDistanceBlock {

	public VariableDirectionPowerPillDistanceBlock(int dir) {
		super(dir);
	}

	@Override
	public String getType() {
		return "Power Pill";
	}

	@Override
	public int[] getTargets(GameFacade gf) {
		return gf.getActivePowerPillsIndices();
	}
}
