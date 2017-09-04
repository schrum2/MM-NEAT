package edu.southwestern.tasks.mspacman.sensors.ghosts.mediators;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.AllThreatsPresentBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.AnyEdibleGhostBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.veryclose.IsCloseToPowerPill;
import edu.southwestern.tasks.mspacman.sensors.blocks.counting.CountEdibleGhostsBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.counting.PillsRemainingBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.counting.PowerPillsRemainingBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.ghosts.GhostBiasBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.ghosts.GhostEdibleBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.ghosts.GhostReuseMsPacManSensorBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.ghosts.GhostVariableDirectionSortedGhostEdibleBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.ghosts.GhostVariableDirectionSortedGhostIncomingBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.ghosts.GhostVariableDirectionSortedGhostTrappedBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.ghosts.distance.GhostVariableDirectionJunctionDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.ghosts.distance.GhostVariableDirectionMsPacManDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.ghosts.distance.GhostVariableDirectionPillDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.ghosts.distance.GhostVariableDirectionPowerPillDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.ghosts.distance.GhostVariableDirectionSortedGhostDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.time.EdibleTimesBlock;
import edu.southwestern.tasks.mspacman.sensors.ghosts.VariableDirectionGhostBlockLoadedInputOutputMediator;

/**
 * @author Jacob Schrum
 */
public class GhostsCheckEachDirectionMediator extends VariableDirectionGhostBlockLoadedInputOutputMediator {

	public GhostsCheckEachDirectionMediator() {
		int numJunctionsToSense = Parameters.parameters.integerParameter("junctionsToSense");
		boolean infiniteEdibleTime = Parameters.parameters.booleanParameter("infiniteEdibleTime");
		boolean imprisonedWhileEdible = Parameters.parameters.booleanParameter("imprisonedWhileEdible");
		// Split ghost sensors: edible and threat
		//boolean split = Parameters.parameters.booleanParameter("specificGhostEdibleThreatSplit");

		blocks.add(new GhostBiasBlock());
		blocks.add(new GhostEdibleBlock());
		// Distances
		blocks.add(new GhostVariableDirectionPillDistanceBlock(0));
		blocks.add(new GhostVariableDirectionPowerPillDistanceBlock(0));
		for (int i = 0; i < numJunctionsToSense; i++) {
			blocks.add(new GhostVariableDirectionJunctionDistanceBlock(i));
		}
		// Sense pacman
		blocks.add(new GhostVariableDirectionMsPacManDistanceBlock());

		// Other Ghosts By Distance (don't sense self)
		for (int i = 0; i < CommonConstants.numActiveGhosts - 1; i++) {
			blocks.add(new GhostVariableDirectionSortedGhostDistanceBlock(i));
			blocks.add(new GhostVariableDirectionSortedGhostIncomingBlock(i));
			blocks.add(new GhostVariableDirectionSortedGhostTrappedBlock(i));
			if (!imprisonedWhileEdible) {
				blocks.add(new GhostVariableDirectionSortedGhostEdibleBlock(i));
			}
		}

		// How to split sensors with ghosts?
		// if (split) {
		// for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
		// // Threat prox
		// blocks.add(new VariableDirectionSortedGhostDistanceBlock(-1, i,
		// false, false));
		// // Edible prox
		// blocks.add(new VariableDirectionSortedGhostDistanceBlock(-1, i, true,
		// false));
		// if (incoming) {
		// // Threat incoming
		// blocks.add(new VariableDirectionSortedGhostIncomingBlock(i, false,
		// false));
		// // Edible incoming
		// blocks.add(new VariableDirectionSortedGhostIncomingBlock(i, true,
		// false));
		// }
		// if (Parameters.parameters.booleanParameter("trapped")) {
		// // Threat trapped
		// blocks.add(new VariableDirectionSortedGhostTrappedBlock(i, false,
		// false));
		// // Edible trapped
		// blocks.add(new VariableDirectionSortedGhostTrappedBlock(i, true,
		// false));
		// }
		// }
		// }
		// // Look ahead
		// blocks.add(new VariableDirectionKStepPillCountBlock());
		// blocks.add(new VariableDirectionKStepJunctionCountBlock());
		// Counts
		blocks.add(new GhostReuseMsPacManSensorBlock(new PowerPillsRemainingBlock(true, false)));
		blocks.add(new GhostReuseMsPacManSensorBlock(new PillsRemainingBlock(true, false)));
		// For limited edible time
		if (!infiniteEdibleTime) {
			blocks.add(new GhostReuseMsPacManSensorBlock(new CountEdibleGhostsBlock(true, false)));
			blocks.add(new GhostReuseMsPacManSensorBlock(new EdibleTimesBlock()));
		}
		// Other
		blocks.add(new GhostReuseMsPacManSensorBlock(new AnyEdibleGhostBlock()));
		blocks.add(new GhostReuseMsPacManSensorBlock(new AllThreatsPresentBlock()));
		blocks.add(new GhostReuseMsPacManSensorBlock(new IsCloseToPowerPill()));
		// High level
		// if (Parameters.parameters.booleanParameter("highLevel")) {
		// blocks.add(new VariableDirectionCountJunctionOptionsBlock());
		// }
	}
}
