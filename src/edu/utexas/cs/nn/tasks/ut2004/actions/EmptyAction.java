/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.ut2004.actions;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;

/**
 *
 * @author Jacob Schrum
 */
public class EmptyAction implements BotAction {

    private final String extra;
    private final boolean stopShooting;

    public EmptyAction() {
        this("");
    }

    public EmptyAction(String extra) {
        this(extra, true);
    }

    public EmptyAction(String extra, boolean stopShooting) {
        this.extra = extra;
        this.stopShooting = stopShooting;
    }

    public String execute(UT2004BotModuleController bot) {
        // Doing "Nothing" means not shooting anymore
        if (stopShooting) {
            bot.getShoot().stopShooting();
        }
        return "[Nothing]" + extra;
    }
}
