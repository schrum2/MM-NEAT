/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.directional.counts;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.util.MiscUtil;

/**
 * handles PO conditions (TODO: test)
 * @author Jacob Schrum
 */
public class VariableDirectionKStepPillCountBlock extends VariableDirectionKStepCountBlock {

	public VariableDirectionKStepPillCountBlock(int dir) {
		super(dir);
	}

	@Override
	public int[] getCountTargets(GameFacade gf) {
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
