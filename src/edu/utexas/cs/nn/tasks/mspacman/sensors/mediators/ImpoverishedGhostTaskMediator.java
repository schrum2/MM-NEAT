package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.SpecificGhostIsEdibleBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.coords.SpecificGhostXOffsetBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.coords.SpecificGhostYOffsetBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.WallDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.VariableDirectionJunctionDistanceBlock;

/**
 *
 * @author Jacob Schrum
 */
public class ImpoverishedGhostTaskMediator extends BlockLoadedInputOutputMediator {

    public ImpoverishedGhostTaskMediator() {
        blocks.add(new BiasBlock());
        for(int i = 0; i < CommonConstants.numActiveGhosts; i++) {
            blocks.add(new SpecificGhostXOffsetBlock(i));
            blocks.add(new SpecificGhostYOffsetBlock(i));
//            blocks.add(new SpecificGhostEdibleTimeBlock(i));
//            blocks.add(new SpecificGhostLairTimeBlock(i));
            blocks.add(new SpecificGhostIsEdibleBlock(i));
        }
        blocks.add(new WallDistanceBlock());
    }
}
