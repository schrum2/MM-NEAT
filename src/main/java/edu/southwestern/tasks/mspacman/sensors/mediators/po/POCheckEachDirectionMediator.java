package edu.southwestern.tasks.mspacman.sensors.mediators.po;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.AnyEdibleGhostBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.veryclose.IsCloseToPowerPill;
import edu.southwestern.tasks.mspacman.sensors.blocks.counting.CountEdibleGhostsBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.counting.PillsRemainingBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.counting.PowerPillsRemainingBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.time.EdibleGhostTimeRemainingPOBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.time.EdibleTimesBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.time.TimeLeftBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionCountJunctionOptionsBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.counts.VariableDirectionKStepJunctionCountBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.counts.VariableDirectionKStepPillCountBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.VariableDirectionJunctionDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.VariableDirectionPillDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.VariableDirectionPowerPillDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.VariableDirectionSortedGhostDistanceBlock;
//NEW PO STUFF
import edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.po.VariableDirectionSortedPossibleGhostDistanceBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.po.VariableDirectionSortedPossibleGhostProbabilityBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.distance.ghosts.specific.po.VariableDirectionSortedPossibleGhostIncomingBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.specific.VariableDirectionSortedGhostTrappedBlock;;

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

		blocks.add(new VariableDirectionPillDistanceBlock(direction));
		blocks.add(new VariableDirectionPowerPillDistanceBlock(direction));
		
		
		for (int i = 0; i < numJunctionsToSense; i++) {
			//Works with PO, this info is always available
			blocks.add(new VariableDirectionJunctionDistanceBlock(direction, i));
		}

		// Ghosts By Distance
		if (Parameters.parameters.booleanParameter("specificGhostProximityOrder")) {
			for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
				
				//if we are using the ghostModel
				if(Parameters.parameters.booleanParameter("useGhostModel")) {
//					// Split ghost sensors: edible and threat
					boolean split = Parameters.parameters.booleanParameter("specificGhostEdibleThreatSplit");
					if (split) {
						
							// Threat prox
							blocks.add(new VariableDirectionSortedPossibleGhostDistanceBlock(i, true, false));
							blocks.add(new VariableDirectionSortedPossibleGhostProbabilityBlock(i, true, false));
							// Edible prox
							blocks.add(new VariableDirectionSortedPossibleGhostDistanceBlock(i, false, true));
							blocks.add(new VariableDirectionSortedPossibleGhostProbabilityBlock(i, false, true));
							if (incoming) {
								// Threat incoming
								blocks.add(new VariableDirectionSortedPossibleGhostIncomingBlock(i, true, false));
								// Edible incoming
								blocks.add(new VariableDirectionSortedPossibleGhostIncomingBlock(i, false, true));
							}
							if (Parameters.parameters.booleanParameter("trapped")) {
								// Threat trapped
								blocks.add(new VariableDirectionSortedGhostTrappedBlock(i, false, false));
							}
						
					} else {
						//this will keep track of the nearest four ghosts, but with probabilities, there can be many more than
						//just four probable ghosts
						blocks.add(new VariableDirectionSortedPossibleGhostDistanceBlock(i));
						blocks.add(new VariableDirectionSortedPossibleGhostProbabilityBlock(i));
					}
				} else {
					blocks.add(new VariableDirectionSortedGhostDistanceBlock(i));
				}
				
				//READD
				if (incoming) {
					//blocks.add(new VariableDirectionSortedGhostIncomingBlock(i));
				}
				if (Parameters.parameters.booleanParameter("trapped")) {
					//blocks.add(new VariableDirectionSortedGhostTrappedBlock(i));
				}
				if (Parameters.parameters.booleanParameter("eTimeVsGDis")) {
					//blocks.add(new VariableDirectionSortedGhostEdibleTimeVsDistanceBlock(i));
				}
				if (!imprisonedWhileEdible) {
					//blocks.add(new VariableDirectionSortedGhostEdibleBlock(i));
				}
			}
		}
		
	
		// Look ahead
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
		//blocks.add(new AllThreatsPresentBlock());
		blocks.add(new IsCloseToPowerPill());
		blocks.add(new TimeLeftBlock());
		blocks.add(new EdibleGhostTimeRemainingPOBlock());
		
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
