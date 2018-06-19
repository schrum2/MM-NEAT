package edu.southwestern.tasks.ut2004.sensors;

import java.util.HashMap;

public class TeamDMSensorModel extends UT2004BlockLoadedSensorModel {

	// TODO: constructor that adds sensor blocks
	
	
	@Override
	public UT2004SensorModel copy() {
		// TODO: Make sure that each copy uses the exact same HashMap reference
		return null;
	}
	
	public void giveTeamInfo(HashMap<String,Double> info) {
		// TODO: loop through the blocks, give the HashMap to blocks that accept it
	}

}
