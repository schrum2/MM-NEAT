package edu.southwestern.tasks.ut2004.controller;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.tasks.ut2004.actions.BotAction;

/**
 * A Bot Controller can be initialized and reset, and most importantly will
 * return a BotAction to perform on a given logic cycle when provided with
 * the bot's module controller via the control method.
 *
 * @author Jacob Schrum
 */
public interface BotController {

	public BotAction control(@SuppressWarnings("rawtypes") UT2004BotModuleController bot);

	public void initialize(@SuppressWarnings("rawtypes") UT2004BotModuleController bot);

	public void reset(@SuppressWarnings("rawtypes") UT2004BotModuleController bot);
}
