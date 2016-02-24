package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.components;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.SplitSpecificGhostBlock;

/**
 * Contains only sensor blocks that are absolutely necessary to all other
 * mediators.
 *
 * @author Jacob Schrum
 */
public class SpecificGhostSensors extends BlockLoadedInputOutputMediator {

    public SpecificGhostSensors() {
        super();
        for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
            blocks.add(new SplitSpecificGhostBlock(i));
        }

//        blocks.add(new SpecificGhostDistancesBlock(true, false));
//        blocks.add(new SpecificGhostProximityBlock(true, false));
//        blocks.add(new SpecificGhostsDirectionBlock(true,true)); // towards
//        blocks.add(new SpecificGhostsDirectionBlock(true,false)); // away
//        if (canEatGhosts) {
//            blocks.add(new SpecificGhostDistancesBlock(false, true));
//            blocks.add(new SpecificGhostProximityBlock(false, true));
//            blocks.add(new SpecificGhostsDirectionBlock(false,true));
//            blocks.add(new SpecificGhostLairTimeBlock(0));
//            blocks.add(new SpecificGhostLairTimeBlock(1));
//            blocks.add(new SpecificGhostLairTimeBlock(2));
//            blocks.add(new SpecificGhostLairTimeBlock(3));
//        }
    }
}
