package edu.southwestern.tasks.mspacman.sensors.directional.specific;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.ghosts.DirectionalGhostComparator;
import edu.southwestern.tasks.mspacman.ghosts.GhostComparator;
import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import java.util.ArrayList;
import java.util.Collections;

/**
 * TODO: test support for popacman. It should support popacman
 * @author Jacob
 */
public class VariableDirectionSortedGhostTrappedBlock extends VariableDirectionBlock {

	private final int order;
	private final boolean edibleClose;
	private final boolean proximityOnly;
	
	/**
	 * TODO: test the support for popacman
	 */
	public VariableDirectionSortedGhostTrappedBlock(int order) {
		this(order, true, true);
	}

	public VariableDirectionSortedGhostTrappedBlock(int order, boolean edibleClose, boolean proximityOnly) {
		super(-1);
		this.order = order;
		this.edibleClose = edibleClose;
		this.proximityOnly = proximityOnly;
	}

	@Override
	public double wallValue() {
		return 0;
	}

	/**
	 * TODO: test the support for popacman
	 */
	@Override
	public double getValue(GameFacade gf) {
		ArrayList<Integer> ghosts = gf.getGhostIndices(edibleClose, proximityOnly);
		if (order >= ghosts.size()) {
			return 0; // Not incoming if in lair
		}
		Collections.sort(ghosts,
				CommonConstants.checkEachAbsoluteDistanceGhostSort ? new GhostComparator(gf, edibleClose, proximityOnly)
						: new DirectionalGhostComparator(gf, edibleClose, proximityOnly, dir));
		return gf.isGhostTrapped(dir, ghosts.get(order)) ? 1 : 0;
	}

	@Override
	public String getLabel() {
		return order + " Closest Ghost Trapped";
	}
}
