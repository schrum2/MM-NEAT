package edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.veryclose;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.util.MiscUtil;

/**
 * TODO: decide what to do for PO conditions
 * @author Jacob Schrum
 */
public class IsCloseToPowerPill extends IsCloseBlock {

	private static final int CLOSE_POWER_PILL_DISTANCE = 10;

	public IsCloseToPowerPill() {
		super(CLOSE_POWER_PILL_DISTANCE);
	}

	@Override
	public int[] getTargets(GameFacade gf) {
		//TODO: what should be returned if no targets are visible?
//		int[] intermediate = gf.getActivePowerPillsIndices();;
//		for(int i = 0; i < intermediate.length; i++) {
//			System.out.println(intermediate[i]);
//		}
		//MiscUtil.waitForReadStringAndEnterKeyPress();
		return gf.getActivePowerPillsIndices();
	}

	@Override
	public String getType() {
		return "Power Pill";
	}
}
