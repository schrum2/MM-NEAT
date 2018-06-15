package edu.southwestern.tasks.mspacman.sensors.directional.specific;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.ghosts.DirectionalGhostComparator;
import edu.southwestern.tasks.mspacman.ghosts.GhostComparator;
import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import java.util.ArrayList;
import java.util.Collections;

/**
 * supports popacman (TODO: test)
 * @author Jacob
 */
public class VariableDirectionSortedGhostIncomingBlock extends VariableDirectionBlock {

	private final int order;
	private final boolean edibleClose;
	private final boolean proximityOnly;

	/**
	 * supports popacman. (TODO: test)
	 * @param order
	 */
	public VariableDirectionSortedGhostIncomingBlock(int order) {
		this(order, true, true);
	}
	
	public VariableDirectionSortedGhostIncomingBlock(int order, boolean edibleClose, boolean proximityOnly) {
		super(-1);
		this.order = order;
		this.edibleClose = edibleClose;
		this.proximityOnly = proximityOnly;
	}

	@Override
	public double wallValue() {
		return 0;
	}

	@Override
	/**
	 * TODO: test popacman support
	 */
	public double getValue(GameFacade gf) {
		ArrayList<Integer> ghosts = gf.getGhostIndices(edibleClose, proximityOnly);
		if (order >= ghosts.size()) {
			return 0; // Not incoming if in lair
		}
		Collections.sort(ghosts,
				CommonConstants.checkEachAbsoluteDistanceGhostSort ? new GhostComparator(gf, edibleClose, proximityOnly)
						: new DirectionalGhostComparator(gf, edibleClose, proximityOnly, dir));
		return gf.isGhostIncoming(dir, ghosts.get(order)) ? 1 : 0;
	}

	@Override
	public String getLabel() {
		return order + " Closest " + (proximityOnly ? "" : (edibleClose ? "Edible " : "Threat ")) + "Ghost Incoming";
	}
}
