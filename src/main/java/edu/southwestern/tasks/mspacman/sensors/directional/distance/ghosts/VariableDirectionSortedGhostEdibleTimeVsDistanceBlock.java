/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts;

import java.util.ArrayList;
import java.util.Collections;

import edu.southwestern.networks.activationfunctions.FullLinearPiecewiseFunction;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.ghosts.DirectionalGhostComparator;
import edu.southwestern.tasks.mspacman.ghosts.GhostComparator;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import oldpacman.game.Constants;

/**
 * TODO: how to handle PO conditions
 * @author Jacob Schrum
 */
public class VariableDirectionSortedGhostEdibleTimeVsDistanceBlock extends VariableDirectionSortedGhostDistanceBlock {

	private final int order;
	private int ghostIndex; // used to pass info between functions, like an
							// extra return

	public VariableDirectionSortedGhostEdibleTimeVsDistanceBlock(int order) {
		this(-1, order);
	}

	public VariableDirectionSortedGhostEdibleTimeVsDistanceBlock(int dir, int order) {
		super(dir);
		this.order = order;
	}

	@Override
	public String getType() {
		return order + " Closest Ghost";
	}

	@Override
	/**
	 * supports popacman (TODO: test)
	 */
	public int[] getTargets(GameFacade gf) {
		ArrayList<Integer> ghosts = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			if (!gf.ghostInLair(i)) {
				ghosts.add(i);
			}
		}
		if (order >= ghosts.size()) {
			return new int[0]; // Target in lair will result in distance of
								// infinity
		}
		Collections.sort(ghosts, CommonConstants.checkEachAbsoluteDistanceGhostSort
				? new GhostComparator(gf, true, true) : new DirectionalGhostComparator(gf, true, true, dir));
		// System.out.println("Time:"+gf.getTotalTime()+":dir:"+dir+":Order:"+order+":ghost:"+ghosts.get(order));
		ghostIndex = ghosts.get(order);
		return new int[] { gf.getGhostCurrentNodeIndex(ghostIndex) };
	}

	@Override
	/**
	 * TODO: how to handle PO conditions
	 */
	public double getValue(GameFacade gf) {
		if (numberToExclude == 0) {
			excludedNodes.clear();
		}
		assert numberToExclude == excludedNodes.size() : "Not excluding the right number of node results: "
				+ numberToExclude + ":" + excludedNodes;
		final int current = gf.getPacmanCurrentNodeIndex();
		final int[] targets = ArrayUtil.setDifference(getTargets(gf), excludedNodes);
		if (targets.length == 0) {
			// excludedNodes.add(-1); // non-existant node
			return -1.0; // Distance is "infinity"
		} else {
			Pair<Integer, int[]> pair = gf.getTargetInDir(current, targets, dir);
			if(pair == new Pair<Integer, int[]>(-1, null)) {
				throw new UnsupportedOperationException("TODO: how to handle PO conditions");
			}
			excludedNodes.add(pair.t1); // Exclude this result from the next
										// call
			int[] path = pair.t2;
			int edibleTime = gf.getGhostEdibleTime(ghostIndex);
			double distance = path.length;
			double result = ((edibleTime - distance) / Constants.EDIBLE_TIME);
			result = FullLinearPiecewiseFunction.fullLinear(result);
			// System.out.println("Distance:"+distance+":result:"+result);
			return result;
		}
	}

	@Override
	public double wallValue() {
		return -1;
	}

	@Override
	public String getLabel() {
		return "Edible Time Vs Distance to " + numberToExclude + " Nearest " + getType();
	}
}
