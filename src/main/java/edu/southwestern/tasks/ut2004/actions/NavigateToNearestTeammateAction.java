package edu.southwestern.tasks.ut2004.actions;

import java.util.HashMap;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;

/**
 * Tells the bot to navigate to the given teammate
 * @author Adina Friedman
 */
public class NavigateToNearestTeammateAction extends NavigateToLocationAction {
//	public Player friend;
//	Location nearestFriend = null;
	HashMap<String,Location> friendLocations;
	public static final int MAX_DISTANCE = 1000;
	
	public void giveFriendLocations(HashMap<String,Location> friendLocations) {
		this.friendLocations = friendLocations;
	}
	
	public static Location locationOfNearestTeammate(@SuppressWarnings("rawtypes") UT2004BotModuleController bot, HashMap<String,Location> friendLocs) {
		Location botLocation = bot.getBot().getLocation();
		double minDistance = MAX_DISTANCE;
		Location nearestFriend = null;
		for(String s: friendLocs.keySet()) {
			Location friendLocation = friendLocs.get(s);
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
	public NavigateToNearestTeammateAction(UT2004BotModuleController bot, HashMap<String,Location> teamLocations) {
		super(locationOfNearestTeammate(bot,teamLocations));
	}
	
	@Override
	/**
	 * tells bot to follow command
	 * @return returns a string identifying which teammate the bot is following
	 */
	public String execute(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		//null check
		//super.execute(bot);
//		if(nearestFriend == null) {
//			return ("do nothing");
//		}
		String original = super.execute(bot);
		//NavigateToLocationAction(locationOfNearestTeammate(bot));
		return ("navigating to teammate: " + original); //friend.getName());
	}
}
