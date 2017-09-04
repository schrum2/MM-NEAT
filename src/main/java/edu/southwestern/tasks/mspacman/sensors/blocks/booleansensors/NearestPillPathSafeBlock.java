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
public class NearestPillPathSafeBlock extends PathSafeBlock {

	public NearestPillPathSafeBlock(GhostControllerFacade ghostModel) {
		super(ghostModel);
	}

	@Override
	public String targetLabel() {
		return "Nearest Pill";
	}

	@Override
	public int getTarget(GameFacade gf, int lastDirection) {
		return gf.getClosestNodeIndexFromNodeIndex(gf.getPacmanCurrentNodeIndex(), gf.getActivePillsIndices());
	}
}
