package edu.southwestern.tasks.ut2004.controller.behaviors;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import edu.southwestern.tasks.ut2004.controller.pathexplorers.WeaponItemPathExplorer;

/**
 * Grab weapons when it sees them
 * @author Jacob Schrum
 */
public class WeaponGrabBehaviorModule extends WeaponItemPathExplorer implements BehaviorModule {

	/**
	 * Tells the bot whether or not to execute this behavior
	 */
	public boolean trigger(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		Item weapon = bot.getItems().getNearestVisibleItem(Category.WEAPON);
		if(weapon == null) return false; // Can't grab a weapon you can't see
		if(bot.getWeaponry().hasWeapon(weapon.getType())) return false; // Don't grab a weapon you have
		return true; // Otherwise, grab it
	}
}
