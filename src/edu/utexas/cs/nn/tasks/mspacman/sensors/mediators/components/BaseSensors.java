package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.components;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.GhostReversalBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.HittingWallBlock;

/**
 * Contains only sensor blocks that are absolutely necessary to all other
 * mediators.
 *
 * @author Jacob Schrum
 */
public class BaseSensors extends BlockLoadedInputOutputMediator {

    public BaseSensors() {
        super();
        blocks.add(new BiasBlock());
        blocks.add(new GhostReversalBlock());
        if (!CommonConstants.eliminateImpossibleDirections) {
            blocks.add(new HittingWallBlock());
        }
    }
}
