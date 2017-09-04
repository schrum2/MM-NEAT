/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.directional.counts;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionKStepApproachingEdibleGhostCountBlock extends VariableDirectionKStepCountBlock {
	private final boolean[] includeGhosts;

	public VariableDirectionKStepApproachingEdibleGhostCountBlock(int dir) {
		this(dir, new boolean[] { true, true, true, true });
	}

	public VariableDirectionKStepApproachingEdibleGhostCountBlock(int dir, boolean[] include) {
		super(dir);
		this.includeGhosts = include;
	}

	@Override
	public int[] getCountTargets(GameFacade gf) {
		return gf.getApproachingEdibleGhostLocations(includeGhosts);
	}

	@Override
	public String getType() {
		return "Approaching Edible Ghost";
	}

	@Override
	public int maxCount(GameFacade gf) {
		return gf.getNumActiveGhosts();
	}
}
