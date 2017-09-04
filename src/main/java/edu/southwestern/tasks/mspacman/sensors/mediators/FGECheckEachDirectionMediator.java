/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.mediators;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.SpecificGhostIsEdibleBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.counting.CountLairGhostsBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.counting.PillsRemainingBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.counting.PowerPillsRemainingBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionCountJunctionOptionsBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.counts.VariableDirectionKStepJunctionCountBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.counts.VariableDirectionKStepPillCountBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.VariableDirectionJunctionDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.VariableDirectionPillDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.VariableDirectionPowerPillDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionSortedGhostDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.specific.VariableDirectionSpecificGhostDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.specific.VariableDirectionSortedGhostEdibleBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.specific.VariableDirectionSortedGhostIncomingBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.specific.VariableDirectionSpecificGhostIncomingBlock;

/**
 *
 * @author Jacob Schrum
 */
public class FGECheckEachDirectionMediator extends VariableDirectionBlockLoadedInputOutputMediator {

	public FGECheckEachDirectionMediator() {
		int direction = -1;
		int numJunctionsToSense = Parameters.parameters.integerParameter("junctionsToSense");

		blocks.add(new BiasBlock());
		// Distances
		blocks.add(new VariableDirectionPillDistanceBlock(direction));
		blocks.add(new VariableDirectionPowerPillDistanceBlock(direction));
		for (int i = 0; i < numJunctionsToSense; i++) {
			blocks.add(new VariableDirectionJunctionDistanceBlock(direction, i));
		}
		// Specific Ghosts
		if (Parameters.parameters.booleanParameter("specific")) {
			for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
				blocks.add(new VariableDirectionSpecificGhostDistanceBlock(direction, i));
				blocks.add(new VariableDirectionSpecificGhostIncomingBlock(i));
				blocks.add(new SpecificGhostIsEdibleBlock(i));
			}
		}
		// Ghosts By Distance
		if (Parameters.parameters.booleanParameter("specificGhostProximityOrder")) {
			for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
				blocks.add(new VariableDirectionSortedGhostDistanceBlock(i));
				blocks.add(new VariableDirectionSortedGhostIncomingBlock(i));
				blocks.add(new VariableDirectionSortedGhostEdibleBlock(i));
			}
		}
		// Look ahead
		blocks.add(new VariableDirectionKStepPillCountBlock(direction));
		blocks.add(new VariableDirectionKStepJunctionCountBlock(direction));
		// Counts
		blocks.add(new PowerPillsRemainingBlock(true, false));
		blocks.add(new PillsRemainingBlock(true, false));
		blocks.add(new CountLairGhostsBlock(true, false));
		// Other
		// blocks.add(new AnyEdibleGhostBlock());
		// High level
		// blocks.add(new
		// VariableDirectionDistanceFromJunctionToGhostBlock(direction, true,
		// true));
		// blocks.add(new
		// VariableDirectionCountAllPillsInKStepsBlock(direction));
		if (Parameters.parameters.booleanParameter("highLevel")) {
			blocks.add(new VariableDirectionCountJunctionOptionsBlock());
		}
		// blocks.add(new VariableDirectionOneStepSafeBlock());
	}
}
