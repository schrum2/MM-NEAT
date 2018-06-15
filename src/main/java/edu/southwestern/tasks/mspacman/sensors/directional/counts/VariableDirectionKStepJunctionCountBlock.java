/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.directional.counts;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 * Supports popacman. JunctionIndices are always available in both PO and non-PO conditions.
 * @author Jacob Schrum
 */
public class VariableDirectionKStepJunctionCountBlock extends VariableDirectionKStepCountBlock {

	public VariableDirectionKStepJunctionCountBlock(int dir) {
		super(dir);
	}

	@Override
	/**
	 * This information is always freely available in both PO and non-PO conditions.
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
