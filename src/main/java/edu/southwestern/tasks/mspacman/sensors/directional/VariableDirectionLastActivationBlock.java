/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional;

import edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.NNDirectionalPacManController;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 * Recalls the previous activation for a particular (relative) direction
 * 
 * @author Jacob
 */
public class VariableDirectionLastActivationBlock extends VariableDirectionBlock {
	private final int offset;

	public VariableDirectionLastActivationBlock(int dir, int offset) {
		super(dir);
		this.offset = offset;
	}

	@Override
	public double wallValue() {
		return 0;
	}

	@Override
	public double getValue(GameFacade gf) {
		return NNDirectionalPacManController.previousPreferences[(dir + offset) % GameFacade.NUM_DIRS];
	}

	@Override
	public String getLabel() {
		return "Last Activation " + relativeDirection(offset);
	}

	public String relativeDirection(int offset) {
		switch (offset) {
		case 0:
			return "Same";
		case 1:
			return "Right";
		case 2:
			return "Opposite";
		case 3:
			return "Left";
		}
		throw new IllegalArgumentException("Offset out of range: " + offset);
	}
}
