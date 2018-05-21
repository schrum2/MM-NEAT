package edu.southwestern.tasks.ut2004.actions;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;

/**
 * This action does nothing, but acts as a template for the other actions 
 * @author Jacob Schrum
 * 
 */
public class EmptyAction implements BotAction {

	private final String extra;
	private final boolean stopShooting;

	public EmptyAction() {
		this("");
	}

	/**
	 * (This method provides the string extra that is included as part of the class)
	 *
	 * @param (extra) (string describing action)
	 * @return (does not return a value)
	 */
	public EmptyAction(String extra) {
		this(extra, true);
	}

	/**
	 * (This method defines what boolean the action will be looking at, along with the string extra)
	 *
	 * @param (extra) (the string given to the method to describe what the bot did)
	 * @param (stopShooting) (the boolean value given to the class)
	 * @return (does not return a value)
	 */
	public EmptyAction(String extra, boolean stopShooting) {
		this.extra = extra;
		this.stopShooting = stopShooting;
	}
	/**
	 * (This function tells the bot to execute the command given)
	 *
	 * @param (bot) (the bot that will execute the command)
	 * @return (what the bot is doing + the descriptive string)
	 */
	public String execute(UT2004BotModuleController bot) {
		// Doing "Nothing" means not shooting anymore
		if (stopShooting) {
			bot.getShoot().stopShooting();
		}
		return "[Nothing]" + extra;
	}
}
