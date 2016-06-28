package edu.utexas.cs.nn.tasks.mspacman.sensors;

import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.hyperneat.SubstrateGhostSensorBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.hyperneat.SubstratePacManSensorBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.hyperneat.SubstratePillsSensorBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.hyperneat.SubstratePowerPillSensorBlock;
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
//		blocks.add(new SubstratePowerPillSensorBlock());
		blocks.add(new SubstrateGhostSensorBlock());
//		blocks.add(new SubstratePacManSensorBlock());
	}
}
