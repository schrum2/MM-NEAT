/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.facades.GhostControllerFacade;

/**
 *
 * @author Jacob Schrum
 */
public class NearestPowerPillPathSafeBlock extends PathSafeBlock {

	public NearestPowerPillPathSafeBlock(GhostControllerFacade ghostModel) {
		super(ghostModel);
	}

	@Override
	public String targetLabel() {
		return "Nearest Power Pill";
	}

	@Override
	public int getTarget(GameFacade gf, int lastDirection) {
		return gf.getClosestNodeIndexFromNodeIndex(gf.getPacmanCurrentNodeIndex(), gf.getActivePowerPillsIndices());
	}
}
