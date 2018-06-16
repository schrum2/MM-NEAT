/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.directional.distance;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.util.MiscUtil;

/**
 * handles PO conditions (TODO: implement PillModel to finish this)
 * @author Jacob Schrum
 */
public class VariableDirectionPillDistanceBlock extends VariableDirectionDistanceBlock {

	public VariableDirectionPillDistanceBlock(int dir) {
		super(dir);
	}

	@Override
	public String getType() {
		return "Pill";
	}

	@Override
	/**
	 * The targets for this sensor are the active pill indicies in the current maze. In non-PO
	 * conditions, we always have access to this information.
	 * 
	 * In PO conditions, if we are using a pill model (see parameters, booleanOption usePillModel), then 
	 * we get the active pill indices from the GameFacade's PillModel (TODO: get PillModel up and running).
	 * 
	 * If we are not using a pill model bu are in PO conditions, we recieve an array of pills that we can see. 
	 * This array could be empty if we cannot see any pills.
	 */
	public int[] getTargets(GameFacade gf) {
		return gf.getActivePillsIndices();
	}
}
