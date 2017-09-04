/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.VariableDirectionDistanceBlock;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionEdibleGhostDistanceBlock extends VariableDirectionDistanceBlock {

	public VariableDirectionEdibleGhostDistanceBlock(int dir) {
		super(dir);
	}

	@Override
	public String getType() {
		return "Edible Ghost";
	}

	@Override
	public int[] getTargets(GameFacade gf) {
		return gf.getEdibleGhostLocations();
	}
}
