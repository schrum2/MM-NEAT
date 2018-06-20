package edu.southwestern.tasks.ut2004.actions;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

/**
 * Tells the bot to follow the nearest teammate
 * @author Adina Friedman
 */
public class FollowEnemy extends NavigateToLocationAction {
	public Player nearestEnemy;
	
	/**
	 * Gives the bot which enemy it needs to follow
	 * @param enemy (teammate to follow)
	 */
	public FollowEnemy(Player enemy) {
		super(enemy.getLocation());
		this.nearestEnemy = enemy;
	}
	
	@Override
	/**
	 * tells bot to follow command
	 * @return returns a string identifying which teammate the bot is following
	 */
	public String execute(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		super.execute(bot);
		return ("Following enemy: " + nearestEnemy.getName());
	}

}
