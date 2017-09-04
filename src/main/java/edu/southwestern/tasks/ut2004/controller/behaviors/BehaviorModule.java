/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.ut2004.controller.behaviors;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.tasks.ut2004.actions.BotAction;

/**
 *
 * @author Jacob Schrum
 */
public interface BehaviorModule {

	public BotAction control(UT2004BotModuleController bot);

	public void initialize(UT2004BotModuleController bot);

	public void reset(UT2004BotModuleController bot);

	public boolean trigger(UT2004BotModuleController bot);
}
