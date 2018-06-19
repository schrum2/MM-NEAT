package edu.southwestern.tasks.ut2004.sensors.blocks.team;

import java.util.HashMap;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import edu.southwestern.tasks.ut2004.Util;
import edu.southwestern.tasks.ut2004.sensors.AcceptsTeamDistances;
import edu.southwestern.tasks.ut2004.sensors.blocks.UT2004SensorBlock;

public class NearestTeammateBlock implements UT2004SensorBlock, AcceptsTeamDistances{
	
	HashMap<String,Location> teamLocation;
	
	@Override
	public void prepareBlock(UT2004BotModuleController bot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int incorporateSensors(UT2004BotModuleController bot, int in, double[] inputs) {
		distanceToClosestFriend(bot);
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * @return returns the distance to the bot's closest ally
	 */
	public double distanceToClosestFriend(UT2004BotModuleController bot){
		double minDistance = 10000;
		Location botLocation = bot.getBot().getLocation();
		for(String s: teamLocation.keySet()) {
		//for each location, find distance btween bot and friend
			Location friendLocation = teamLocation.get(s);
			double friendDistance = friendLocation.getDistance(botLocation);
			minDistance = (Math.min(friendDistance, minDistance));
			//location.getDistance()
		}
		return minDistance;
		//Player friend = bot.
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
	public void giveTeamDistances(HashMap<String,Location> distances){
		teamLocation = distances;
	}

	

}
