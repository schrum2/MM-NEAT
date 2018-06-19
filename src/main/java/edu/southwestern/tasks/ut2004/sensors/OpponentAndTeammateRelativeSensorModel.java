package edu.southwestern.tasks.ut2004.sensors;

import java.util.HashMap;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import edu.southwestern.tasks.ut2004.sensors.blocks.*;
import edu.southwestern.tasks.ut2004.sensors.blocks.team.NearestTeammateBlock;

/**
 * Finds the the bot's position and velocity with respect to the opponent it's facing 
 * @author Jacob Schrum
 */
public class OpponentAndTeammateRelativeSensorModel extends UT2004BlockLoadedSensorModel {

	HashMap<String,Location> teammateLocations;
	
	/**
	 * creates the block of sensors
	 */
	public OpponentAndTeammateRelativeSensorModel() { //adds the specific sensor blocks
		this(null);
	}
	
	public OpponentAndTeammateRelativeSensorModel(HashMap<String,Location> teammateLocations) { //adds the specific sensor blocks
		blocks.add(new AutoRayTraceSensorBlock());
		blocks.add(new PieSliceAgentSensorBlock(true)); //true means that the bot senses an ENEMY nearby
		blocks.add(new PieSliceAgentSensorBlock(false)); //false means that the bot senses a FRIEND nearby
		blocks.add(new NearestAgentDistanceBlock(true)); //true means that the bot senses an ENEMY nearby
		blocks.add(new NearestAgentDistanceBlock(false)); //false means that the bot senses a FRIEND nearby
		blocks.add(new AgentBehaviorBlock(true)); //true means that the bot senses an ENEMY nearby
		blocks.add(new AgentBehaviorBlock(false)); //false means that the bot senses a FRIEND nearby
		blocks.add(new SelfAwarenessBlock());
		// Sensors that use shared memory on the client side
//		NearestTeammateBlock ntb = new NearestTeammateBlock();
//		ntb.giveTeamLocations(teammateLocations);
//		blocks.add(ntb);

		// Saved so that it can be part of any copies that are made
		this.teammateLocations = teammateLocations;
	}

	/**
	 * creates a copy of the sensor model
	 */
	public UT2004SensorModel copy() {
		return new OpponentAndTeammateRelativeSensorModel(teammateLocations);
	}
	
	public void giveTeamInfo(HashMap<String,Location> info) {
		for(UT2004SensorBlock block : blocks) {
			if(block instanceof AcceptsTeamDistances) {
				((AcceptsTeamDistances) block).giveTeamLocations(info);
			}
		}
	}

}
