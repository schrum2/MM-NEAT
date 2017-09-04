package edu.southwestern.tasks.mspacman.sensors.mediators.components;

import edu.southwestern.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.time.EdibleTimesBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.time.LairTimesBlock;

/**
 * Nearest distance to objects of interest
 *
 * @author Jacob Schrum
 */
public class GhostTimeSensors extends BlockLoadedInputOutputMediator {

	public GhostTimeSensors(boolean senseEdibleGhosts) {
		super();
		blocks.add(new LairTimesBlock(new boolean[] { false, false, false, true }));
		if (senseEdibleGhosts) {
			blocks.add(new EdibleTimesBlock(new boolean[] { false, false, false, true }));
		}
		// blocks.add(new NextEdibleTimeBlock()); // gives info even if ghosts
		// not currently edible
	}
}
