/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.directional.specific;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionBlock;

/**
 * TODO: Doesn't yet support popacman
 * @author Jacob
 */
public class VariableDirectionSpecificGhostIncomingBlock extends VariableDirectionBlock {
	private final int ghostIndex;

	public VariableDirectionSpecificGhostIncomingBlock(int ghostIndex) {
		super(-1);
		this.ghostIndex = ghostIndex;
	}

	@Override
	public double wallValue() {
		return 0;
	}

	@Override
	/**
	 * TODO: needs major retooling to support popacman
	 */
	public double getValue(GameFacade gf) {
		return gf.isGhostIncoming(dir, ghostIndex) ? 1 : 0;
	}

	@Override
	public String getLabel() {
		return "Ghost " + ghostIndex + " Incoming";
	}

}
