/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.mediators;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.AnyEdibleGhostBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.counts.VariableDirectionKStepJunctionCountBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.VariableDirectionJunctionDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.fromjunction.VariableDirectionDistanceFromJunctionToGhostBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.specific.VariableDirectionSpecificGhostDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.specific.VariableDirectionSpecificGhostIncomingBlock;

/**
 *
 * @author Jacob Schrum
 */
public class TvsECheckEachDirectionMediator extends VariableDirectionBlockLoadedInputOutputMediator {

	public TvsECheckEachDirectionMediator() {
		int irrelevantStartingDirection = -1;

		blocks.add(new BiasBlock());
		// Distances
		blocks.add(new VariableDirectionJunctionDistanceBlock(irrelevantStartingDirection));
		// Specific Ghosts
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			blocks.add(new VariableDirectionSpecificGhostDistanceBlock(irrelevantStartingDirection, i));
			blocks.add(new VariableDirectionSpecificGhostIncomingBlock(i));
			// blocks.add(new SpecificGhostIsEdibleBlock(i));
		}
		// Look ahead
		blocks.add(new VariableDirectionDistanceFromJunctionToGhostBlock(irrelevantStartingDirection, true, true));
		blocks.add(new AnyEdibleGhostBlock());
		blocks.add(new VariableDirectionKStepJunctionCountBlock(irrelevantStartingDirection));
	}
}
