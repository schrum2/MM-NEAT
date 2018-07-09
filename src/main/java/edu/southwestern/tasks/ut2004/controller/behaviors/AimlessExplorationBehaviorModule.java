package edu.southwestern.tasks.ut2004.controller.behaviors;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.tasks.ut2004.controller.RandomNavPointPathExplorer;

/**
 * Tells the bot to move around the map using the RandomNavPointPathExplorer
 * @author Jacob Schrum
 */
public class AimlessExplorationBehaviorModule extends RandomNavPointPathExplorer implements BehaviorModule {

	/**
	 * tells the bot whether or not to execute this action
	 */
	public boolean trigger(UT2004BotModuleController bot) {
		// Bottom level behavior
		return true;
	}
}
