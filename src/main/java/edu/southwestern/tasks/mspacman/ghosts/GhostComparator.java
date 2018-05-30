/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.ghosts;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import java.util.Comparator;

/**
 * Sort ghosts based on proximity to pacman, with the option to focus only on
 * edible or non-edible ghosts.
 * supports popacman (TODO: test)
 *
 * @author Jacob Schrum
 */
public class GhostComparator implements Comparator<Integer> {

	private final GameFacade gs;
	private final int current;
	private final int sign;
	private final boolean proximityOnly;

	/**
	 * Supports popacman (TODO: test)
	 * @param gs
	 * @param edibleClose
	 * @param proximityOnly
	 */
	public GhostComparator(GameFacade gs, boolean edibleClose, boolean proximityOnly) {
		this.gs = gs;
		this.current = gs.getPacmanCurrentNodeIndex();
		this.sign = edibleClose ? 1 : -1;
		this.proximityOnly = proximityOnly;
	}
	
	/**
	 * TODO: is this how we want to handle PO conditions? see the else
	 */
	public int compare(Integer o1, Integer o2) {
		if (!proximityOnly && gs.isGhostEdible(o1) && !gs.isGhostEdible(o2)) {
			return -1 * sign;
		} else if (!proximityOnly && !gs.isGhostEdible(o1) && gs.isGhostEdible(o2)) {
			return 1 * sign;
		} else {
			
			// If getGhostCurrentNodeIndex is -1 then the ghost is not visible and the effective distance is infinity
			// NOTE: Maybe improve this in the future with some kind of memory that makes a better guess, but for now say infinity
			double o1Dist; 
			double o2Dist; 
			
			int o1Index = gs.getGhostCurrentNodeIndex(o1);
			//If o1 is not visible, then say than the ghost it represents is infinitely far away
			if(o1Index == -1) {
				o1Dist = Integer.MAX_VALUE;
			} else {
				//else get its actual distance
				o1Dist = gs.getPathDistance(current, gs.getGhostCurrentNodeIndex(o1));
			}
			
			int o2Index = gs.getGhostCurrentNodeIndex(o2);
			//If o2 is not visible, then say than the ghost it represents is infinitely far away
			if(o2Index == -1) {
				o2Dist = Integer.MAX_VALUE;
			} else {
				//else get its actual distance
				o2Dist = gs.getPathDistance(current, gs.getGhostCurrentNodeIndex(o1));
			}
			
			if (o2Dist == -1 && o1Dist > -1) {
				return -1;
			} else if (o1Dist == -1 && o2Dist > -1) {
				return 1;
			}
			return (int) Math.signum(o1Dist - o2Dist);
		}
	}
}
