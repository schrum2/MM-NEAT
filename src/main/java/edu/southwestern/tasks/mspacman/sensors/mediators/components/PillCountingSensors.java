package edu.southwestern.tasks.mspacman.sensors.mediators.components;

import edu.southwestern.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.PowerPillsClearedBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.counting.PillsRemainingBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.counting.PowerPillsRemainingBlock;

/**
 * Counts of pills
 *
 * @author Jacob Schrum
 */
public class PillCountingSensors extends BlockLoadedInputOutputMediator {

	public PillCountingSensors(boolean pillsPresent, boolean canEatGhosts) {
		super();
		if (pillsPresent) {
			blocks.add(new PillsRemainingBlock(true, false));
		}
		if (canEatGhosts) {
			blocks.add(new PowerPillsRemainingBlock(true, false));
			blocks.add(new PowerPillsClearedBlock()); // redundant?
		}
	}
}
