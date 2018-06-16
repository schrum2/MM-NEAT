package edu.southwestern.tasks.ut2004.controller;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.tasks.ut2004.actions.BotAction;
import edu.southwestern.tasks.ut2004.actions.EmptyAction;

/**
 * Bot that stands still and does nothing
 *
 * @author Jacob
 */
public class DummyController implements BotController {

	@Override
	/**
	 * creates the controller with no actions
	 */
	public BotAction control(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		return new EmptyAction();
	}

	@Override
	/**
	 * initializes the controller (does nothing here, because the bot is stationary)
	 */
	public void initialize(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
	}

	@Override
	/**
	 * resets the controller
	 */
	public void reset(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
	}
}
