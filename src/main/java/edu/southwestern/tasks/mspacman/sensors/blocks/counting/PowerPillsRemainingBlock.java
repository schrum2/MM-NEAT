/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.blocks.counting;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.util.MiscUtil;

/**
 * TODO: decide what to do in PO conditions
 * @author Jacob Schrum
 */
public class PowerPillsRemainingBlock extends TargetPortionRemainingBlock {

	public PowerPillsRemainingBlock(boolean portion, boolean inverse) {
		super(portion, inverse);
	}

	@Override
	public int getTargetMax(GameFacade gf) {
		return gf.getNumberOfPowerPills();
	}

	@Override
	public int getTargetCurrent(GameFacade gf) {
		int[] intermediate = gf.getActivePowerPillsIndices();;
		for(int i = 0; i < intermediate.length; i++) {
			System.out.println(intermediate[i]);
		}
		MiscUtil.waitForReadStringAndEnterKeyPress();
		return gf.getActivePowerPillsIndices().length;
	}

	@Override
	public String getTargetType() {
		return "Power Pill";
	}
}
