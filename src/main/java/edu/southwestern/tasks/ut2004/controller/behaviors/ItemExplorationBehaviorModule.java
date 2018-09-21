package edu.southwestern.tasks.ut2004.controller.behaviors;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.tasks.ut2004.controller.pathexplorers.RandomItemPathExplorer;

/**
 * instructs the bot to move around the map picking up items
 * @author Jacob Schrum
 */
public class ItemExplorationBehaviorModule extends RandomItemPathExplorer implements BehaviorModule {

	/**
	 * Tells the bot whether or not to execute this behavior
	 */
	public boolean trigger(UT2004BotModuleController bot) {
		// Bottom level behavior
		return true;
	}
}
