package edu.southwestern.tasks.ut2004.weapons;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;

/**
 *Dictates the methods needed for weapon managers
 * @author Jacob Schrum
 */
public interface UT2004WeaponManager {

	public void prepareWeaponPreferences(UT2004BotModuleController bot);

	public ItemType chooseWeapon(UT2004BotModuleController bot);

	public UT2004WeaponManager copy();
}
