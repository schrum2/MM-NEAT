package edu.southwestern.tasks.ut2004.sensors;

import java.util.HashMap;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import edu.southwestern.tasks.ut2004.sensors.blocks.*;
import edu.southwestern.tasks.ut2004.sensors.blocks.team.AverageTeammateHealthBlock;
import edu.southwestern.tasks.ut2004.sensors.blocks.team.DistanceToNearestTeammateBlock;
import edu.southwestern.tasks.ut2004.sensors.blocks.team.HighestTeammateHealthBlock;
import edu.southwestern.tasks.ut2004.sensors.blocks.team.LowestTeammateHealthBlock;

/**
 * Finds the the bot's position and velocity with respect to the opponent it's facing 
 * @author Jacob Schrum
 */
public class OpponentAndTeammateRelativeSensorModel extends UT2004BlockLoadedSensorModel {

	public HashMap<String,Location> teammateLocations;
	public HashMap<String,Double> teammateHealths;
	
	/**
	 * creates the block of sensors
	 */
	public OpponentAndTeammateRelativeSensorModel() { //adds the specific sensor blocks
		this(null, null);
	}
	
	public OpponentAndTeammateRelativeSensorModel(HashMap<String,Location> teammateLocations, HashMap<String,Double> teammateHealths) { //adds the specific sensor blocks
		blocks.add(new AutoRayTraceSensorBlock());
		blocks.add(new PieSliceAgentSensorBlock(true)); //true means that the bot senses an ENEMY nearby
		blocks.add(new PieSliceAgentSensorBlock(false)); //false means that the bot senses a FRIEND nearby
		blocks.add(new NearestAgentDistanceBlock(true)); //true means that the bot senses an ENEMY nearby
		blocks.add(new NearestAgentDistanceBlock(false)); //false means that the bot senses a FRIEND nearby
		blocks.add(new AgentBehaviorBlock(true)); //true means that the bot senses an ENEMY nearby
		blocks.add(new AgentBehaviorBlock(false)); //false means that the bot senses a FRIEND nearby
		blocks.add(new SelfAwarenessBlock());
		// Sensors that use shared memory on the client side
		DistanceToNearestTeammateBlock ntb = new DistanceToNearestTeammateBlock();
		ntb.giveTeamLocations(teammateLocations);
		blocks.add(ntb);
		HighestTeammateHealthBlock hth = new HighestTeammateHealthBlock();
		hth.giveTeamHealthLevels(teammateHealths);
		blocks.add(hth);
		LowestTeammateHealthBlock lth = new LowestTeammateHealthBlock();
		lth.giveTeamHealthLevels(teammateHealths);
		blocks.add(lth);
		AverageTeammateHealthBlock ath = new AverageTeammateHealthBlock();
		ath.giveTeamHealthLevels(teammateHealths);
		blocks.add(ath);

		// Saved so that it can be part of any copies that are made
		this.teammateLocations = teammateLocations;
		this.teammateHealths = teammateHealths;
		//TODO: FIGURE OUT WHY THIS !@#$%^&* ISN'T WORKING
	}

	/**
	 * creates a copy of the sensor model
	 */
	public UT2004SensorModel copy() {
		OpponentAndTeammateRelativeSensorModel copy = new OpponentAndTeammateRelativeSensorModel(teammateLocations, teammateHealths);
		assert teammateLocations != null : "Don't copy null team information";
		return copy;
	}
	
	public void giveTeamLocations(HashMap<String,Location> locs) {
		teammateLocations = locs;
		for(UT2004SensorBlock block : blocks) {
			if(block instanceof AcceptsTeamLocations) {
				((AcceptsTeamLocations) block).giveTeamLocations(locs);
			}
		}
	}

	public void giveTeamHelathLevels(HashMap<String,Double> healthLevels) {
		teammateHealths = healthLevels;
		for(UT2004SensorBlock block : blocks) {
			if(block instanceof AcceptsTeamHealthLevels) {
				((AcceptsTeamHealthLevels) block).giveTeamHealthLevels(healthLevels);
			}
		}
	}
}
