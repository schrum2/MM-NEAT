package edu.southwestern.tasks.ut2004.actions;

import java.util.HashMap;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

/**
 * Tells the bot to navigate to the given teammate
 * @author Adina Friedman
 */
public class NavigateToNearestTeammate extends NavigateToLocationAction {
	public Player friend;
	Location nearestFriend = null;
	HashMap<String,Location> friendLocations;
	public static final int MAX_DISTANCE = 1000;
	
	public void giveFriendLocations(HashMap<String,Location> friendLocations) {
		this.friendLocations = friendLocations;
	}
	
	public Location locationOfNearestTeammate(UT2004BotModuleController bot) {
		Location botLocation = bot.getBot().getLocation();
		double minDistance = MAX_DISTANCE;
		for(String s: friendLocations.keySet()) {
			Location friendLocation = friendLocations.get(s);
			if(friendLocation == botLocation) {
				continue;
			}
			double friendDistance = friendLocation.getDistance(botLocation);
			if(friendDistance < minDistance) {
				nearestFriend = friendLocation;
			}			
		}
		return nearestFriend;
	}
	
	
	/**
	 * @param teamLocations (hashmap containing the locations of teammates)
	 */
	public NavigateToNearestTeammate(UT2004BotModuleController bot, HashMap<String,Location> teamLocations) {
		super(locationOfNearestTeammate(bot));
	}
	
	@Override
	/**
	 * tells bot to follow command
	 * @return returns a string identifying which teammate the bot is following
	 */
	public String execute(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		//null check
		//super.execute(bot);
		if(nearestFriend == null) {
			return ("do nothing");
		}
		super.execute(bot);
		//NavigateToLocationAction(locationOfNearestTeammate(bot));
		return ("navigating to teammate: " + friend.getName());
	}
}
