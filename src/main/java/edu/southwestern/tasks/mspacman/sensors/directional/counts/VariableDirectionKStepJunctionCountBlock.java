/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.directional.counts;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 * supports popacman (TODO: test)
 * @author Jacob Schrum
 */
public class VariableDirectionKStepJunctionCountBlock extends VariableDirectionKStepCountBlock {

	public VariableDirectionKStepJunctionCountBlock(int dir) {
		super(dir);
	}

	@Override
	/**
	 * supports popacman (TODO: test)
	 */
	public int[] getCountTargets(GameFacade gf) {
		return gf.getJunctionIndices();
	}

	@Override
	public String getType() {
		return "Junction";
	}

	@Override
	public int maxCount(GameFacade gf) {
		return (int) Math.ceil(stepCount / 7.0); // Empirically based on most
													// tightly packed junctions
													// areas
	}
}
