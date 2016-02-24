package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.components;

import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.time.TimeLeftBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.time.TimeSinceLastGhostReversalBlock;

/**
 * Nearest distance to objects of interest
 *
 * @author Jacob Schrum
 */
public class MazeTimeSensors extends BlockLoadedInputOutputMediator {

    public MazeTimeSensors() {
        super();
        blocks.add(new TimeLeftBlock());
        blocks.add(new TimeSinceLastGhostReversalBlock());
    }
}
