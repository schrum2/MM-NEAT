package edu.southwestern.tasks.ut2004.controller;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.tasks.ut2004.actions.BotAction;
import edu.southwestern.tasks.ut2004.actions.EmptyAction;
import edu.southwestern.tasks.ut2004.controller.behaviors.BehaviorModule;
import java.util.ArrayList;

/**
 * Creates and populates an ArrayList of behavior modules
 * @author Jacob Schrum
 */
public class BehaviorListController implements BotController {

	/**
	 * constructs an ArrayList of the bot's behaviour modules
	 */
	public ArrayList<BehaviorModule> behaviors;

	/**
	 * initializes the BehaviorListController as a blank ArrayList
	 */
	public BehaviorListController() {
		this.behaviors = new ArrayList<BehaviorModule>();
	}

	/**
	 * initializes the BehaviorListController with a given ArrayList
	 * @param behaviors (ArrayList of behaviors)
	 */
	public BehaviorListController(ArrayList<BehaviorModule> behaviors) {
		this.behaviors = behaviors;
	}

	/**
	 * selects an action for the bot to execute
	 * @return returns a controller for the bot
	 */
	public BotAction control(UT2004BotModuleController bot) {
		for (int i = 0; i < behaviors.size(); i++) {
			BehaviorModule mod = behaviors.get(i);
			if (mod.trigger(bot)) {
				return mod.control(bot);
			}
		}
		return new EmptyAction();
	}

	/**
	 * initializes the controller
	 */
	public void initialize(UT2004BotModuleController bot) {
		for (int i = 0; i < behaviors.size(); i++) {
			behaviors.get(i).initialize(bot);
		}
	}

	/**
	 * resets the controller to be reprogrammed
	 */
	public void reset(UT2004BotModuleController bot) {
		for (int i = 0; i < behaviors.size(); i++) {
			behaviors.get(i).reset(bot);
		}
	}
}
