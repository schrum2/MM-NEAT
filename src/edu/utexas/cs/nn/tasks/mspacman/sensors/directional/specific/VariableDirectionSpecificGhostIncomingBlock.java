/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.specific;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;

/**
 *
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
	public double getValue(GameFacade gf) {
		return gf.isGhostIncoming(dir, ghostIndex) ? 1 : 0;
	}

	@Override
	public String getLabel() {
		return "Ghost " + ghostIndex + " Incoming";
	}

}
