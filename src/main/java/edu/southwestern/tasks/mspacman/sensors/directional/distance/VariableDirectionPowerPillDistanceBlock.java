package edu.southwestern.tasks.mspacman.sensors.directional.distance;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 * handles PO conditions (TODO: implement PillModel and add support for tracking power pills)
 * @author Jacob Schrum
 */
public class VariableDirectionPowerPillDistanceBlock extends VariableDirectionDistanceBlock {

	public VariableDirectionPowerPillDistanceBlock(int dir) {
		super(dir);
	}

	@Override
	public String getType() {
		return "Power Pill";
	}

	@Override
	/**
	 * In non-PO conditions, this method returns an array that has all of the active power pill locations, as that
	 * information is freely available. In PO conditions, this method could return two things depending on whether or not 
	 * we are using a pill model. 
	 * 
	 * If we are using a pill model (see parameters, boolean usePillModel), this would return the locations of the active
	 * power pills according to our model (TODO: add support for tracking power pills in pill model).
	 * 
	 * If we are in PO conditions but are not using a pill model, this method returns an array of the locations of active power
	 * pills that we can see. If we cannot see any power pills, the returned array is empty.
	 */
	public int[] getTargets(GameFacade gf) {
		return gf.getActivePowerPillsIndices();
	}
}
