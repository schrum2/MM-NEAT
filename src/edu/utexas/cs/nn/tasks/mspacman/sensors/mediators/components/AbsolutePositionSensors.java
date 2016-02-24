package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.components;

import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.raw.CurrentSectorBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.raw.MirroredXPosBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.raw.MirroredYPosBlock;

/**
 *
 * @author Jacob Schrum
 */
public class AbsolutePositionSensors extends BlockLoadedInputOutputMediator {

    public AbsolutePositionSensors(int width, int height) {
        super();
        blocks.add(new CurrentSectorBlock(width, height));
        blocks.add(new MirroredXPosBlock());
        blocks.add(new MirroredYPosBlock());
        //blocks.add(new RelativeCoordinatesBlock()); // redundant?
    }
}
