/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.ut2004.controller.behaviors;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.utexas.cs.nn.tasks.ut2004.controller.RandomNavPointPathExplorer;

/**
 *
 * @author Jacob Schrum
 */
public class AimlessExplorationBehaviorModule extends RandomNavPointPathExplorer implements BehaviorModule {

    public boolean trigger(UT2004BotModuleController bot) {
        // Bottom level behavior
        return true;
    }
}
