package edu.southwestern.tasks.ut2004.actions;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import mockcz.cuni.pogamut.Client.AgentBody;

/**
 * Tells the bot to follow the nearest enemy and shoot at it until it dies, or the bot needs to get health
 * @author Adina Friedman
 */
public class PursueEnemyAction extends NavigateToLocationAction {
	public Player nearestEnemy;
	public Player target;
	public boolean shoot;
	
	/**
	 * 
	 * @param enemy
	 * @param shoot
	 */
	public PursueEnemyAction(Player enemy, boolean shoot) {
		super(enemy.getLocation());
		this.nearestEnemy = enemy;
		this.shoot = shoot;
	}
	
	@Override
	/**
	 * tells bot to follow command
	 * @return returns a string identifying which enemy the bot is following
	 */
	public String execute(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		AgentBody body = OldActionWrapper.getAgentBody(bot);
		super.execute(bot);
		return ("Following enemy: " + nearestEnemy.getName());
	}

}
