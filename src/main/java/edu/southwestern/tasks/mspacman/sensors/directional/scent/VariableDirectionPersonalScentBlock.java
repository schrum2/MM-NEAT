/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.southwestern.tasks.mspacman.sensors.directional.scent;

import edu.southwestern.tasks.mspacman.data.ScentPath;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionBlock;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionPersonalScentBlock extends VariableDirectionBlock {

	public VariableDirectionPersonalScentBlock(int dir) {
		super(dir);
	}

	@Override
	public double wallValue() {
		return 0;
	}

	@Override
	public double getValue(GameFacade gf) {
		int current = gf.getPacmanCurrentNodeIndex();
		int[] neighbors = gf.neighbors(current);
		return Math.min(ScentPath.scents.getScent(neighbors[dir]), 1.0);
	}

	@Override
	public String getLabel() {
		return "Personal Scent";
	}

}
