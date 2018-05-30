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
public class VariableDirectionPillDistanceBlock extends VariableDirectionDistanceBlock {

	public VariableDirectionPillDistanceBlock(int dir) {
		super(dir);
	}

	@Override
	public String getType() {
		return "Pill";
	}

	@Override
	public int[] getTargets(GameFacade gf) {
		//TODO: what should be returned if no targets are visible?
		int[] intermediate = gf.getActivePillsIndices();;
		for(int i = 0; i < intermediate.length; i++) {
			System.out.println(intermediate[i]);
		}
		MiscUtil.waitForReadStringAndEnterKeyPress();
		return gf.getActivePillsIndices();
	}
}
