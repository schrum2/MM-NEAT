package edu.southwestern.tasks.mspacman.sensors.directional.counts;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 * handles PO conditions. (TODO: implement PillModel in order to have this function correctly)
 * @author Jacob Schrum
 */
public class VariableDirectionKStepPillCountBlock extends VariableDirectionKStepCountBlock {

	public VariableDirectionKStepPillCountBlock(int dir) {
		super(dir);
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
	public int[] getCountTargets(GameFacade gf) {
		return gf.getActivePillsIndices();
	}

	@Override
	public String getType() {
		return "Pill";
	}

	@Override
	public int maxCount(GameFacade gf) {
		return (int) Math.ceil(stepCount / 4.0); // Empirically based on
													// distance between pills
	}
}
