package edu.southwestern.tasks.ut2004.controller.behaviors;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.tasks.ut2004.actions.BotAction;

/**
 *
 * @author Jacob Schrum
 */
public interface BehaviorModule {

	public BotAction control(@SuppressWarnings("rawtypes") UT2004BotModuleController bot);

	public void initialize(@SuppressWarnings("rawtypes") UT2004BotModuleController bot);

	public void reset(@SuppressWarnings("rawtypes") UT2004BotModuleController bot);

	public boolean trigger(@SuppressWarnings("rawtypes") UT2004BotModuleController bot);
}
