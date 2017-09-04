package edu.southwestern.tasks.mspacman.sensors.blocks.distance;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.util.stats.Average;

/**
 *
 * @author Jacob Schrum
 */
public abstract class AverageDistanceBlock extends StatDistanceBlock {

	public AverageDistanceBlock() {
		super(new Average(), GameFacade.MAX_DISTANCE);
	}
}
