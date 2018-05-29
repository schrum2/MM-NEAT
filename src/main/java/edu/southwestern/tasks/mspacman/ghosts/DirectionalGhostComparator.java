package edu.southwestern.tasks.mspacman.ghosts;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import java.util.Comparator;

/**
 * Sort ghosts based on proximity to pacman, with the option to focus only on
 * edible or non-edible ghosts
 *
 * @author Jacob Schrum
 */
public class DirectionalGhostComparator implements Comparator<Integer> {

	private final GameFacade gs;
	private final int current;
	private final int sign;
	private final boolean proximityOnly;
	private final int dir;

	public DirectionalGhostComparator(GameFacade gs, boolean edibleClose, boolean proximityOnly, int dir) {
		this.gs = gs;
		this.current = gs.getPacmanCurrentNodeIndex();
		this.sign = edibleClose ? 1 : -1;
		this.proximityOnly = proximityOnly;
		this.dir = dir;
	}
	
	/**
	 * Supports popacman
	 */
	public int compare(Integer o1, Integer o2) {
		if (!proximityOnly && gs.isGhostEdible(o1) && !gs.isGhostEdible(o2)) {
			return -1 * sign;
		} else if (!proximityOnly && !gs.isGhostEdible(o1) && gs.isGhostEdible(o2)) {
			return 1 * sign;
		} else {
			// If getGhostCurrentNodeIndex is -1 then the ghost is not visible and the effective distance is infinity
			// NOTE: Maybe improve this in the future with some kind of memory that makes a better guess, but for now say infinity
			int o1Dist;
			int o2Dist;
			
			//If you can't see ghost, it is infinitely far away. Else, get its actual distance.
			if(gs.getGhostCurrentNodeIndex(o1) == -1) {
				o1Dist = Integer.MAX_VALUE; 
			} else {
				o1Dist = gs.getDirectionalPath(current, gs.getGhostCurrentNodeIndex(o1), dir).length;	
			}
			
			//If you can't see ghost, it is infinitely far away. Else, get its actual distance.
			if(gs.getGhostCurrentNodeIndex(o2) == -1) {
				o2Dist = Integer.MAX_VALUE; 
			} else {
				o2Dist = gs.getDirectionalPath(current, gs.getGhostCurrentNodeIndex(o2), dir).length;	
			}
			
			if (o2Dist == 0 && o1Dist > 0) {
				return -1;
			} else if (o1Dist == 0 && o2Dist > 0) {
				return 1;
			}
			return (int) Math.signum(o1Dist - o2Dist);
		}
	}
}
