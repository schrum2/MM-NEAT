package edu.southwestern.tasks.mspacman.sensors.mediators.po;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.AllThreatsPresentBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.AnyEdibleGhostBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.veryclose.IsCloseToPowerPill;
import edu.southwestern.tasks.mspacman.sensors.blocks.counting.CountEdibleGhostsBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.counting.PillsRemainingBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.counting.PowerPillsRemainingBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.time.EdibleTimesBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionCountJunctionOptionsBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.counts.VariableDirectionKStepJunctionCountBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.counts.VariableDirectionKStepPillCountBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.VariableDirectionJunctionDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.VariableDirectionPillDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.VariableDirectionPowerPillDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionSortedGhostDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionSortedGhostEdibleTimeVsDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.specific.VariableDirectionSpecificGhostDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.specific.VariableDirectionSortedGhostEdibleBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.specific.VariableDirectionSortedGhostIncomingBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.specific.VariableDirectionSortedGhostTrappedBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.specific.VariableDirectionSpecificGhostIncomingBlock;

/**
 * TODO: Describe
 * 
 * @author Will Price
 */
public class POCheckEachDirectionMediator extends VariableDirectionBlockLoadedInputOutputMediator {

	public POCheckEachDirectionMediator() {
		int direction = -1;
		int numJunctionsToSense = Parameters.parameters.integerParameter("junctionsToSense");
		boolean incoming = Parameters.parameters.booleanParameter("incoming");
		boolean infiniteEdibleTime = Parameters.parameters.booleanParameter("infiniteEdibleTime");
		boolean imprisonedWhileEdible = Parameters.parameters.booleanParameter("imprisonedWhileEdible");

		blocks.add(new BiasBlock());
		// Distances
		//TODO: implement pill model correctly for this to work
		blocks.add(new VariableDirectionPillDistanceBlock(direction)); // ASSUME PILL MODEL INFLUENCES THESE IN GAMEFACADE
		//TODO: implement pill model and add support for tracking power pills for this to work
		blocks.add(new VariableDirectionPowerPillDistanceBlock(direction));
		
		
		for (int i = 0; i < numJunctionsToSense; i++) {
			//Works with PO, this info is always available
			blocks.add(new VariableDirectionJunctionDistanceBlock(direction, i));
		}

		// Ghosts By Distance
		if (Parameters.parameters.booleanParameter("specificGhostProximityOrder")) {
			for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
				
				// CHANGE TO USE GHOST MODEL INSTEAD
				blocks.add(new VariableDirectionSortedGhostDistanceBlock(i));
				
				if (incoming) {
					blocks.add(new VariableDirectionSortedGhostIncomingBlock(i));
				}
				if (Parameters.parameters.booleanParameter("trapped")) {
					blocks.add(new VariableDirectionSortedGhostTrappedBlock(i));
				}
				if (Parameters.parameters.booleanParameter("eTimeVsGDis")) {
					blocks.add(new VariableDirectionSortedGhostEdibleTimeVsDistanceBlock(i));
				}
				if (!imprisonedWhileEdible) {
					blocks.add(new VariableDirectionSortedGhostEdibleBlock(i));
				}
			}
		}
		// Split ghost sensors: edible and threat
		boolean split = Parameters.parameters.booleanParameter("specificGhostEdibleThreatSplit");
		if (split) {
			for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
				// Threat prox
				blocks.add(new VariableDirectionSortedGhostDistanceBlock(-1, i, false, false));
				// Edible prox
				blocks.add(new VariableDirectionSortedGhostDistanceBlock(-1, i, true, false));
				if (incoming) {
					// Threat incoming
					blocks.add(new VariableDirectionSortedGhostIncomingBlock(i, false, false));
					// Edible incoming
					blocks.add(new VariableDirectionSortedGhostIncomingBlock(i, true, false));
				}
				if (Parameters.parameters.booleanParameter("trapped")) {
					// Threat trapped
					blocks.add(new VariableDirectionSortedGhostTrappedBlock(i, false, false));
					// Edible trapped
					blocks.add(new VariableDirectionSortedGhostTrappedBlock(i, true, false));
				}
			}
		}
		
		// Look ahead
		//TODO: implement pill model
		blocks.add(new VariableDirectionKStepPillCountBlock(direction));
		//Works with PO, this information is always available
		blocks.add(new VariableDirectionKStepJunctionCountBlock(direction));
		
		// Counts
		blocks.add(new PowerPillsRemainingBlock(true, false));
		blocks.add(new PillsRemainingBlock(true, false));
		
		// For limited edible time
		if (!infiniteEdibleTime) {
			blocks.add(new CountEdibleGhostsBlock(true, false));
			blocks.add(new EdibleTimesBlock());
		}
		// Other
		blocks.add(new AnyEdibleGhostBlock());
		blocks.add(new AllThreatsPresentBlock());
		blocks.add(new IsCloseToPowerPill());
		// High level
		// blocks.add(new
		// VariableDirectionDistanceFromJunctionToGhostBlock(direction, true, true));
		// blocks.add(new VariableDirectionCountAllPillsInKStepsBlock(direction));
		if (Parameters.parameters.booleanParameter("highLevel")) {
			blocks.add(new VariableDirectionCountJunctionOptionsBlock());
			// These sensors don't seem to help
			// blocks.add(new VariableDirectionPowerPillBlocksJunctionBlock(direction));
			// blocks.add(new VariableDirectionGhostBlocksJunctionBlock(direction));
		}
		// blocks.add(new VariableDirectionOneStepSafeBlock());
	}
}
