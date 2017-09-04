package edu.utexas.cs.nn.tasks.mspacman.sensors;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.hyperneat.SubstrateEdibleSensorBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.hyperneat.SubstrateFullScreenPowerPillSensorBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.hyperneat.SubstrateGhostSensorBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.hyperneat.SubstratePacManSensorBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.hyperneat.SubstratePillsSensorBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.hyperneat.SubstratePowerPillSensorBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.hyperneat.SubstrateThreatSensorBlock;
/**
 * Mediator class for hyperNEAT msPacMan.
 *  Defines the sensor blocks, inputs and their components
 * @author Lauren Gillespie
 *
 */
public class MsPacManHyperNEATMediator extends BlockLoadedInputOutputMediator {

	/**
	 * Constructor simply aggregates all the
	 *  necessary sensor blocks together
	 */
	public MsPacManHyperNEATMediator() {
		blocks.add(new SubstratePillsSensorBlock());
		if(Parameters.parameters.booleanParameter("pacmanFullScreenPowerInput")) {
			blocks.add(new SubstrateFullScreenPowerPillSensorBlock());	
		}else {
			blocks.add(new SubstratePowerPillSensorBlock());
		}
		if(Parameters.parameters.booleanParameter("pacmanBothThreatAndEdibleSubstrate")) {
			blocks.add(new SubstrateThreatSensorBlock());
			blocks.add(new SubstrateEdibleSensorBlock());
		} else {
			blocks.add(new SubstrateGhostSensorBlock());
		}
		blocks.add(new SubstratePacManSensorBlock());
	}
}
