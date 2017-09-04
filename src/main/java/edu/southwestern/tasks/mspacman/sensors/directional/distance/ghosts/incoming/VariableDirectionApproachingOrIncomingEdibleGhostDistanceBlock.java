/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.incoming;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.VariableDirectionDistanceBlock;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionApproachingOrIncomingEdibleGhostDistanceBlock extends VariableDirectionDistanceBlock {

	public VariableDirectionApproachingOrIncomingEdibleGhostDistanceBlock(int dir) {
		super(dir);
	}

	@Override
	public String getType() {
		return "Approaching/Incoming Edible Ghost";
	}

	@Override
	public int[] getTargets(GameFacade gf) {
		return gf.getApproachingOrIncomingEdibleGhostLocations(dir);
	}
}
