package edu.southwestern.tasks.mspacman.sensors.directional;

import org.apache.commons.lang3.ArrayUtils;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.util.datastructures.Pair;

/**
 *
 * @author Jacob
 */
public class VariableDirectionPillsBeforeJunctionBlock extends VariableDirectionBlock {

	public VariableDirectionPillsBeforeJunctionBlock(int dir) {
		super(dir);
	}

	@Override
	public double wallValue() {
		return 0;
	}

	@Override
	public double getValue(GameFacade gf) {
		Pair<Integer, int[]> pair = gf.getTargetInDir(gf.getPacmanCurrentNodeIndex(), gf.getJunctionIndices(), dir);
		int pillCount = 0;
		int[] activePills = gf.getActivePillsIndices();
		for (int i = 0; i < pair.t2.length; i++) {
			if (ArrayUtils.contains(activePills, pair.t2[i])) {
				pillCount++;
			}
		}
		return pillCount / 27.0; // Maze 3 long corridor holds 26 pills (MAGIC NUMBER!)
	}

	@Override
	public String getLabel() {
		return "Pills Before Junction";
	}
}
