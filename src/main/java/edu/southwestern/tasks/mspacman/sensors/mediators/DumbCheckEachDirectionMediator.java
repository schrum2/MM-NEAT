/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.mediators;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.counting.PillsRemainingBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.counting.PowerPillsRemainingBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.distance.lair.SpecificGhostLairDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.time.SpecificGhostEdibleTimeBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.time.SpecificGhostLairTimeBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionPillsBeforeJunctionBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.counts.*;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.*;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.specific.VariableDirectionSpecificGhostDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.specific.VariableDirectionSpecificGhostIncomingBlock;

/**
 *
 * @author Jacob Schrum
 */
public class DumbCheckEachDirectionMediator extends VariableDirectionBlockLoadedInputOutputMediator {

	public DumbCheckEachDirectionMediator() {
		int irrelevantStartingDirection = -1;

		blocks.add(new BiasBlock());
		// Distances
		blocks.add(new VariableDirectionPillDistanceBlock(irrelevantStartingDirection));
		blocks.add(new VariableDirectionPowerPillDistanceBlock(irrelevantStartingDirection));
		blocks.add(new VariableDirectionJunctionDistanceBlock(irrelevantStartingDirection));
		// Specific Ghosts
		for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
			blocks.add(new VariableDirectionSpecificGhostDistanceBlock(irrelevantStartingDirection, i));
			blocks.add(new VariableDirectionSpecificGhostIncomingBlock(i));
			blocks.add(new SpecificGhostEdibleTimeBlock(i));
			blocks.add(new SpecificGhostLairTimeBlock(i));
			blocks.add(new SpecificGhostLairDistanceBlock(i));
		}
		// Look ahead
		blocks.add(new VariableDirectionKStepPillCountBlock(irrelevantStartingDirection));
		blocks.add(new VariableDirectionKStepPowerPillCountBlock(irrelevantStartingDirection));
		blocks.add(new VariableDirectionKStepJunctionCountBlock(irrelevantStartingDirection));
		// Misc
		blocks.add(new VariableDirectionPillsBeforeJunctionBlock(irrelevantStartingDirection));
		blocks.add(new PillsRemainingBlock(true, false));
		blocks.add(new PowerPillsRemainingBlock(true, false));
	}
}
