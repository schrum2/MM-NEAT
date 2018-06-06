package edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.po;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.VariableDirectionDistanceBlock;
import edu.southwestern.util.datastructures.Triple;
import pacman.game.Constants.MOVE;

/**
 * Possible ghosts are those that we can see and those that we have a probability for.
 * 
 * @author Will Price
 *
 */
public class VariableDirectionSortedPossibleGhostDistanceBlock extends VariableDirectionDistanceBlock{

	//NOTES:
	//SEE ANONYMOUS COMPARATOR IN:
	//NearestEscapeNodeThreatDistanceDifferencesBlock line 35
	//SEE HOW TO COMPARE GHOST DISTANCES IN:
	//DirectionalGhostComparator.compare
	//IMPLEMENT GET TARGETS IN A SIMILAR FASHION TO
	//VariableDirectionSortedGhostDistanceBlock
	
	private final int order;
	
	/**
	 * Creates a new VariableDirectionSortedPossibleGhostDistanceBlock from the other constructor:
	 * VariableDirectionSortedGhostDistanceBlock(int dir, int order, boolean edibleClose, boolean proximityOnly)
	 * Sets dir to -1.	
	 * Sets edibleClose to true.	
	 * Sets proximityOnly to true.	
	 * @param order sets order in the other constructor.
	 */
	public VariableDirectionSortedPossibleGhostDistanceBlock(int order) {
		this(-1, order);
	}
	
	/**
	 * Handles PO pacman. Considers possible ghost locations on top of ghost locations that are visible.
	 * @param dir the direction this block is observing
	 * @param order the orderith (nth) ghost away
	 * @param edibleClose true means looking at edible ghosts, false means looking at threat ghosts
	 * @param proximityOnly whether or not proximity matters more than type of ghost: edible or threat. Overrides edibleClose if true.
	 */
	public VariableDirectionSortedPossibleGhostDistanceBlock(int dir, int order) {
		super(dir);
		this.order = order;
	}

	@Override
	public String getType() {
		return order + " Closest Possible Ghost";
	}

	@Override
	public int[] getTargets(GameFacade gf) {
		ArrayList<Triple<Integer, MOVE, Double>> ghosts = gf.getPossibleGhostInfo();
		
		if (order >= ghosts.size()) {
			return new int[0]; // Target in lair will result in distance of
								// infinity
		}
		
		Collections.sort(ghosts, new Comparator<Triple<Integer, MOVE, Double>>(){

			@Override
			public int compare(Triple<Integer, MOVE, Double> arg0, Triple<Integer, MOVE, Double> arg1) {
				return arg0.t1.compareTo(arg1.t1);
			}
		});
		
		//returns the shortest path to the order (1st, 2nd, 3rd, etc) possible ghost away
		//OLD
		//return new int[] { gf.getGhostCurrentNodeIndex(ghosts.get(order)) };
		return new int[] { ghosts.get(order).t1 };
	}

}
