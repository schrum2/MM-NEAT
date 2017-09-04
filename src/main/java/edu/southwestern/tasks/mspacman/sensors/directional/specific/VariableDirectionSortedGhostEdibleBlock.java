/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.directional.specific;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.ghosts.DirectionalGhostComparator;
import edu.southwestern.tasks.mspacman.ghosts.GhostComparator;
import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Jacob
 */
public class VariableDirectionSortedGhostEdibleBlock extends VariableDirectionBlock {

	private final int order;

	public VariableDirectionSortedGhostEdibleBlock(int order) {
		super(-1);
		this.order = order;
	}

	@Override
	public double wallValue() {
		return 0;
	}

	@Override
	public double getValue(GameFacade gf) {
		ArrayList<Integer> ghosts = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			if (!gf.ghostInLair(i)) {
				ghosts.add(i);
			}
		}
		if (order >= ghosts.size()) {
			return 0; // Not incoming if in lair
		}
		Collections.sort(ghosts, CommonConstants.checkEachAbsoluteDistanceGhostSort
				? new GhostComparator(gf, true, true) : new DirectionalGhostComparator(gf, true, true, dir));
		return gf.isGhostEdible(ghosts.get(order)) ? 1 : 0;
	}

	@Override
	public String getLabel() {
		return order + " Closest Ghost Edible";
	}
}
