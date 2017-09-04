package edu.utexas.cs.nn.tasks.ut2004.controller;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.utexas.cs.nn.tasks.ut2004.actions.BotAction;
import edu.utexas.cs.nn.tasks.ut2004.actions.EmptyAction;

/**
 * Bot that stands still and does nothing
 *
 * @author Jacob
 */
public class DummyController implements BotController {

        @Override
	public BotAction control(UT2004BotModuleController bot) {
		return new EmptyAction();
	}

        @Override
	public void initialize(UT2004BotModuleController bot) {
	}

        @Override
	public void reset(UT2004BotModuleController bot) {
	}
}
