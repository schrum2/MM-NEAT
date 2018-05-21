package edu.southwestern.tasks.ut2004.actions;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

/**
 * This class contains the action directing the bot to move to a location on the map  
 *
 * @author Jacob Schrum
 */
public class NavigateToLocationAction implements BotAction {

	private final ILocated target;
	/**
	 * This method sets the target location for the bot
	 * 
	 * @param target location that needs to be reached
	 */
	public NavigateToLocationAction(ILocated target) {
		this.target = target;
	}

	/**
	 * Tells the bot to execute the command and travel to location
	 * 
	 * @param bot bot to execute command
	 */
	public String execute(UT2004BotModuleController bot) {
		bot.getNavigation().navigate(target);
		return "[Goto " + target.getLocation() + "]"
				+ (target instanceof NavPoint
						? "[" + ((NavPoint) target).getId().getStringId() + "]"
								+ (((NavPoint) target).isInvSpot() ? ((NavPoint) target).getItemClass().getName() : "")
						: "");
	}
}
