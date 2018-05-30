/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.directional.counts;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.util.MiscUtil;

/**
 * TODO: what to do for PO conditions
 * @author Jacob Schrum
 */
public class VariableDirectionKStepPillCountBlock extends VariableDirectionKStepCountBlock {

	public VariableDirectionKStepPillCountBlock(int dir) {
		super(dir);
	}

	@Override
	public int[] getCountTargets(GameFacade gf) {
		//TODO: what should be returned if no targets are visible?
		int[] intermediate = gf.getActivePillsIndices();;
		for(int i = 0; i < intermediate.length; i++) {
			System.out.println(intermediate[i]);
		}
		MiscUtil.waitForReadStringAndEnterKeyPress();
		return gf.getActivePillsIndices();
	}

	@Override
	public String getType() {
		return "Pill";
	}

	@Override
	public int maxCount(GameFacade gf) {
		return (int) Math.ceil(stepCount / 4.0); // Empirically based on
													// distance between pills
	}
}
