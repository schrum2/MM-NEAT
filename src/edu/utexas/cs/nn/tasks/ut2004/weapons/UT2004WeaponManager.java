/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.ut2004.weapons;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;

/**
 *
 * @author He_Deceives
 */
public interface UT2004WeaponManager {

	public void prepareWeaponPreferences(UT2004BotModuleController bot);

	public ItemType chooseWeapon(UT2004BotModuleController bot);

	public UT2004WeaponManager copy();
}
