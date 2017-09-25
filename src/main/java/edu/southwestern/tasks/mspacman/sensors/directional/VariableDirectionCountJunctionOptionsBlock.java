package edu.southwestern.tasks.mspacman.sensors.directional;

import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.directional.reachfirst.VariableDirectionCloserToTargetThanThreatGhostBlock;
import edu.southwestern.util.datastructures.Pair;

/**
 * 
 * @author Jacob Schrum
 */
public class VariableDirectionCountJunctionOptionsBlock extends VariableDirectionBlock {

	public VariableDirectionCountJunctionOptionsBlock() {
		this(-1);
	}

	public VariableDirectionCountJunctionOptionsBlock(int dir) {
		super(dir);
	}

	@Override
	public double getValue(GameFacade gf) {
		int[] junctions = gf.getJunctionIndices();
		// Closest junction
		Pair<Integer, int[]> closest = gf.getTargetInDir(gf.getPacmanCurrentNodeIndex(), junctions, dir);
		int[] ghostsToCheck = new int[] { 0, 1, 2, 3 };
		if (!VariableDirectionCloserToTargetThanThreatGhostBlock.canReachClosestTargetSafelyInDirection(gf,
				new int[] { closest.t1 }, dir, ghostsToCheck)) {
			return 0;
		}
		int current = gf.getPacmanCurrentNodeIndex();
		// Neighbors of the closest junction
		int[] neighbors = gf.neighbors(closest.t1);
		ArrayList<Integer> d2 = new ArrayList<Integer>(GameFacade.NUM_DIRS);
		for (int i = 0; i < neighbors.length; i++) {
			if (neighbors[i] != -1) {
				// Closest directional junction, from the closest junction
				// (depth 2)
				Pair<Integer, int[]> closestD2 = gf.getTargetInDir(closest.t1, junctions, i);
				if (current != closestD2.t1 // Don't start from junction and then return to it
						&& !ArrayUtils.contains(closestD2.t2, current) // In fact, don't reverse direction either
						&& VariableDirectionCloserToTargetThanThreatGhostBlock.canReachClosestTargetSafelyInDirection(
								gf, new int[] { closestD2.t1 }, dir, ghostsToCheck)) {
					// if (CommonConstants.watch) {
					// gf.addLines(Color.CYAN, current, closestD2.t1);
					// }
					d2.add(closestD2.t1);
				}
			}
		}
		// System.out.println(dir + ":" + d2);
		return (d2.size() * 1.0) / GameFacade.NUM_DIRS;
	}

	@Override
	public String getLabel() {
		return "Options After Junction";
	}

	@Override
	public double wallValue() {
		return 0;
	}
}
