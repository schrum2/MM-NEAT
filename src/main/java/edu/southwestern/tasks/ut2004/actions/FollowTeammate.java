package edu.southwestern.tasks.ut2004.actions;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

/**
 * Tells the bot to follow the nearest teammate
 * @author Adina Friedman
 */
public class FollowTeammate extends NavigateToLocationAction {
	public Player nearestFriend;
	
	/**
	 * Gives the bot which teammate it needs to follow
	 * @param friend (teammate to follow)
	 */
	public FollowTeammate(Player friend) {
		super(friend.getLocation());
		this.nearestFriend = friend;
	}
	
	@Override
	/**
	 * tells bot to follow command
	 * @return returns a string identifying which teammate the bot is following
	 */
	public String execute(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		super.execute(bot);
		return ("Following teammate: " + nearestFriend.getName());
	}

}
