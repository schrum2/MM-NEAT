/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.directional.distance;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.util.MiscUtil;

/**
 * TODO: what to do in PO conditions
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
		//TODO: what should be returned if no targets are visible?
//		int[] intermediate = gf.getActivePowerPillsIndices();;
//		for(int i = 0; i < intermediate.length; i++) {
//			System.out.println(intermediate[i]);
//		}
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		return gf.getActivePowerPillsIndices();
	}
}
