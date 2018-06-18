package edu.southwestern.tasks.ut2004.sensors;

import edu.southwestern.tasks.ut2004.sensors.blocks.*;

/**
 * Finds the the bot's position and velocity with respect to the opponent it's facing 
 * @author Jacob Schrum
 */
public class OpponentAndTeammateRelativeSensorModel extends UT2004BlockLoadedSensorModel {

	/**
	 * creates the block of sensors
	 */
	public OpponentAndTeammateRelativeSensorModel() { //adds the specific sensor blocks
		blocks.add(new AutoRayTraceSensorBlock());
		blocks.add(new PieSliceAgentSensorBlock(false)); //true means that the bot senses an ENEMY nearby
		blocks.add(new NearestAgentDistanceBlock(false)); //true means that the bot senses an ENEMY nearby
		blocks.add(new AgentBehaviorBlock(false)); //true means that the bot senses an ENEMY nearby
		blocks.add(new SelfAwarenessBlock());
	}

	/**
	 * creates a copy of the sensor model
	 */
	public UT2004SensorModel copy() {
		return new OpponentAndTeammateRelativeSensorModel();
	}
}
