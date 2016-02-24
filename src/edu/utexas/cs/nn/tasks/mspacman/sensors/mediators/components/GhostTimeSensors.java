package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.components;

import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.time.EdibleTimesBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.time.LairTimesBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.time.NextEdibleTimeBlock;

/**
 * Nearest distance to objects of interest
 *
 * @author Jacob Schrum
 */
public class GhostTimeSensors extends BlockLoadedInputOutputMediator {

    public GhostTimeSensors(boolean senseEdibleGhosts) {
        super();
        blocks.add(new LairTimesBlock(new boolean[]{false, false, false, true}));
        if (senseEdibleGhosts) {
            blocks.add(new EdibleTimesBlock(new boolean[]{false, false, false, true}));
        }
        //blocks.add(new NextEdibleTimeBlock()); // gives info even if ghosts not currently edible
    }
}
