/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.ut2004.controller;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.tasks.ut2004.actions.BotAction;
import edu.southwestern.tasks.ut2004.actions.EmptyAction;
import edu.southwestern.tasks.ut2004.controller.behaviors.BehaviorModule;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class BehaviorListController implements BotController {

	public ArrayList<BehaviorModule> behaviors;

	public BehaviorListController() {
		this.behaviors = new ArrayList<BehaviorModule>();
	}

	public BehaviorListController(ArrayList<BehaviorModule> behaviors) {
		this.behaviors = behaviors;
	}

	public BotAction control(UT2004BotModuleController bot) {
		for (int i = 0; i < behaviors.size(); i++) {
			BehaviorModule mod = behaviors.get(i);
			if (mod.trigger(bot)) {
				return mod.control(bot);
			}
		}
		return new EmptyAction();
	}

	public void initialize(UT2004BotModuleController bot) {
		for (int i = 0; i < behaviors.size(); i++) {
			behaviors.get(i).initialize(bot);
		}
	}

	public void reset(UT2004BotModuleController bot) {
		for (int i = 0; i < behaviors.size(); i++) {
			behaviors.get(i).reset(bot);
		}
	}
}
