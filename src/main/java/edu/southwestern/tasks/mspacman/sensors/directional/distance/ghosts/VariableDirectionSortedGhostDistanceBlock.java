package edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.ghosts.DirectionalGhostComparator;
import edu.southwestern.tasks.mspacman.ghosts.GhostComparator;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.VariableDirectionDistanceBlock;
import java.util.ArrayList;
import java.util.Collections;

/**
 * handles popacman (TODO: test the handling of PO conditions)
 * @author Jacob Schrum
 */
public class VariableDirectionSortedGhostDistanceBlock extends VariableDirectionDistanceBlock {

	private final int order;
	private final boolean edibleClose;
	private final boolean proximityOnly;
	
	/**
	 * Creates a new VariableDirectionSortedGhostDistanceBlock from the other constructor:
	 * VariableDirectionSortedGhostDistanceBlock(int dir, int order, boolean edibleClose, boolean proximityOnly)
	 * Sets dir to -1.	
	 * Sets edibleClose to true.	
	 * Sets proximityOnly to true.	
	 * @param order sets order in the other constructor.
	 */
	public VariableDirectionSortedGhostDistanceBlock(int order) {
		this(-1, order, true, true);
	}
	
	/**
	 * Handles PO pacman
	 * @param dir the direction this block is observing
	 * @param order the orderith (nth) ghost away
	 * @param edibleClose
	 * @param proximityOnly whether or not proximity matters more than type of ghost: edible or threat
	 */
	public VariableDirectionSortedGhostDistanceBlock(int dir, int order, boolean edibleClose, boolean proximityOnly) {
		super(dir);
		this.order = order;
		this.edibleClose = edibleClose;
		this.proximityOnly = proximityOnly;
	}

	@Override
	public String getType() {
		return order + " Closest " + (proximityOnly ? "" : (edibleClose ? "Edible " : "Threat ")) + "Ghost";
	}

	@Override
	/**
	 * TODO: what to do in PO conditions
	 */
	public int[] getTargets(GameFacade gf) {
		
		ArrayList<Integer> ghosts = gf.getGhostIndices(edibleClose, proximityOnly);
		if (order >= ghosts.size()) {
			return new int[0]; // Target in lair will result in distance of
								// infinity
		}
		Collections.sort(ghosts,
				CommonConstants.checkEachAbsoluteDistanceGhostSort ? new GhostComparator(gf, edibleClose, proximityOnly)
						: new DirectionalGhostComparator(gf, edibleClose, proximityOnly, dir));
		// System.out.println("Time:"+gf.getTotalTime()+":dir:"+dir+":Order:"+order+":ghost:"+ghosts.get(order));
		
		//returns the shortest path to the order (1st, 2nd, 3rd, etc) ghost away
		return new int[] { gf.getGhostCurrentNodeIndex(ghosts.get(order)) };
	}
}
