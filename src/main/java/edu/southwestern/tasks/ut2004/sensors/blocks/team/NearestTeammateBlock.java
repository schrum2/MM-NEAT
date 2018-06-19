package edu.southwestern.tasks.ut2004.sensors.blocks.team;

import java.util.HashMap;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.tasks.ut2004.Util;
import edu.southwestern.tasks.ut2004.sensors.AcceptsTeamDistances;
import edu.southwestern.tasks.ut2004.sensors.blocks.UT2004SensorBlock;

public class NearestTeammateBlock implements UT2004SensorBlock, AcceptsTeamDistances{
	
	public static final int MAX_DISTANCE = 1000;

	HashMap<String,Location> teamLocation;
	
	
	@Override
	public void prepareBlock(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
	}

	@Override
	public int incorporateSensors(@SuppressWarnings("rawtypes") UT2004BotModuleController bot, int in, double[] inputs) {
		try {
			inputs[in++] = Util.scale(distanceToClosestFriend(bot), MAX_DISTANCE);
			inputs[in++] = Util.scale(flatDistanceToClosestFriend(bot), MAX_DISTANCE);
		}catch(Exception e) {
			e.printStackTrace();
			assert false : "No exceptions should occur when getting these sensor values " + e;
		}
		return in;
	}
	
	/**
	 * @return returns the distance to the bot's closest ally
	 */
	public double distanceToClosestFriend(@SuppressWarnings("rawtypes") UT2004BotModuleController bot){
		double minDistance = MAX_DISTANCE;
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
	
	/**
	 * @return returns the 2D distance to the bot's closest ally (ignores z axis distance)
	 */
	public double flatDistanceToClosestFriend(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		double minFlatDistance = MAX_DISTANCE;
		Location botLocation = bot.getBot().getLocation();
		for(String s: teamLocation.keySet()) {
			//for each location, find distance btween bot and friend
			Location friendLocation = teamLocation.get(s);
			double friendFlatDistance = friendLocation.getDistance2D(botLocation);
			minFlatDistance = (Math.min(friendFlatDistance, minFlatDistance));
			//location.getDistance()
		}
		return minFlatDistance;
	}

	@Override
	public int incorporateLabels(int in, String[] labels) {
		labels[in++] = "Distance to nearest friend";
		labels[in++] = "2D Distance to nearest friend";
		return in;
	}

	@Override
	public int numberOfSensors() {
		return 2;
	}

	@Override
	public void giveTeamLocations(HashMap<String,Location> distances){
		teamLocation = distances;
	}
}
