/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.directional.distance;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 * Supports popacman. JunctionIndices are always available in both PO and non-PO conditions.
 * @author Jacob Schrum
 */
public class VariableDirectionJunctionDistanceBlock extends VariableDirectionDistanceBlock {

	public VariableDirectionJunctionDistanceBlock(int dir) {
		this(dir, 0);
	}

	public VariableDirectionJunctionDistanceBlock(int dir, int exclude) {
		super(dir, exclude);
	}

	@Override
	public String getType() {
		return "Junction";
	}

	@Override
	/**
	 * This information is always freely available in both PO and non-PO conditions.
	 */
	public int[] getTargets(GameFacade gf) {
		return gf.getJunctionIndices();
	}
}
