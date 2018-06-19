package edu.southwestern.tasks.ut2004.sensors.blocks.team;

import java.util.HashMap;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import edu.southwestern.tasks.ut2004.Util;
import edu.southwestern.tasks.ut2004.sensors.AcceptsTeamDistances;
import edu.southwestern.tasks.ut2004.sensors.blocks.UT2004SensorBlock;

public class NearestTeammateBlock implements UT2004SensorBlock, AcceptsTeamDistances{
	
	HashMap<String,Location> teamDistances;
	
	@Override
	public void prepareBlock(UT2004BotModuleController bot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int incorporateSensors(UT2004BotModuleController bot, int in, double[] inputs) {
		Location loc = bot.getBot().getLocation();
		
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int incorporateLabels(int in, String[] labels) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numberOfSensors() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void giveTeamDistances(HashMap<String, Double> distances) {
		teamDistances = distances;
	}

	

}
