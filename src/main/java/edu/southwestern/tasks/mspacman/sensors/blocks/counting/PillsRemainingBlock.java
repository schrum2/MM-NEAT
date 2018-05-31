package edu.southwestern.tasks.mspacman.sensors.blocks.counting;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.util.MiscUtil;

/**
 * TODO: decide what to do for PO conditions
 * @author Jacob Schrum
 */
public class PillsRemainingBlock extends TargetPortionRemainingBlock {

	public PillsRemainingBlock(boolean portion, boolean inverse) {
		super(portion, inverse);
	}

	@Override
	public int getTargetMax(GameFacade gf) {
		return gf.getNumberOfPills();
	}

	@Override
	public int getTargetCurrent(GameFacade gf) {
		//What should happen if an empty array is returned? TODO
//		int[] intermediate = gf.getActivePillsIndices();;
//		for(int i = 0; i < intermediate.length; i++) {
//			System.out.println(intermediate[i]);
//		}
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		return gf.getActivePillsIndices().length;
	}

	@Override
	public String getTargetType() {
		return "Pill";
	}
}
