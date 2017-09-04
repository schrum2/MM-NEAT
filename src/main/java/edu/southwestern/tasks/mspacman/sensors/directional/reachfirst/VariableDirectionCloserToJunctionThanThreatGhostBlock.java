/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.directional.reachfirst;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import java.awt.Color;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionCloserToJunctionThanThreatGhostBlock
		extends VariableDirectionCloserToTargetThanThreatGhostBlock {

	public VariableDirectionCloserToJunctionThanThreatGhostBlock(int dir) {
		super(dir);
	}

	public VariableDirectionCloserToJunctionThanThreatGhostBlock(int dir, int[] ghosts) {
		super(dir, ghosts);
	}

	@Override
	public double getValue(GameFacade gf) {
		double result = super.getValue(gf);
		if (CommonConstants.watch && result > 0) {
			int current = gf.getPacmanCurrentNodeIndex();
			gf.addLines(Color.green, current, gf.getTargetInDir(current, gf.getJunctionIndices(), dir).t1);
		}
		return result;
	}

	@Override
	public String getTargetType() {
		return "Junction";
	}

	@Override
	public int[] getTargets(GameFacade gf) {
		return gf.getJunctionIndices();
	}
}
