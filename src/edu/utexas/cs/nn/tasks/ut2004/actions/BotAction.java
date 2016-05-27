package edu.utexas.cs.nn.tasks.ut2004.actions;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;

/**
 *
 * @author Jacob Schrum
 */
public interface BotAction {

	/**
	 * Bot executes action and returns a String description of what it did
	 *
	 * @param bot
	 *            bot
	 * @return description of action
	 */
	public String execute(UT2004BotModuleController bot);
}
