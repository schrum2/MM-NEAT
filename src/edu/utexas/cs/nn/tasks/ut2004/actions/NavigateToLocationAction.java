/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.ut2004.actions;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

/**
 *
 * @author Jacob Schrum
 */
public class NavigateToLocationAction implements BotAction {

    private final ILocated target;

    public NavigateToLocationAction(ILocated target) {
        this.target = target;
    }

    public String execute(UT2004BotModuleController bot) {
        bot.getNavigation().navigate(target);
        return "[Goto " + target.getLocation() + "]" + (target instanceof NavPoint ? "[" + ((NavPoint) target).getId().getStringId() + "]" + (((NavPoint) target).isInvSpot() ? ((NavPoint) target).getItemClass().getName() : "") : "");
    }
}
