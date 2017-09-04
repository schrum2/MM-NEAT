/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.ut2004.sensors.blocks;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.WeaponDescriptor;

/**
 * Meant to return lots of useful information about the currently equipped
 * weapon, but these weapon stats are quite confusing. Many probably are not
 * important, and some may even give weird/erroneous data. Focuses only on ammo
 * info.
 *
 * @author Jacob Schrum
 */
public class WeaponAmmoInfoBlock implements UT2004SensorBlock {

	public void prepareBlock(UT2004BotModuleController bot) {
	}

	public int incorporateSensors(UT2004BotModuleController bot, int in, double[] inputs) {
		Weaponry weaponry = bot.getWeaponry();
		Weapon w = weaponry.getCurrentWeapon();
		WeaponDescriptor wd = w.getDescriptor();

		// Primary ammo
		// inputs[in++] = wd.getPriAmmoClipSize();
		inputs[in++] = wd.getPriAmmoPerFire();
		inputs[in++] = (wd.getPriMaxAmount() * 1.0) / w.getPrimaryAmmo();

		// Secondary ammo (not different for most weapons)
		// inputs[in++] = wd.getSecAmmoClipSize();
		inputs[in++] = wd.getSecAmmoPerFire();
		inputs[in++] = (wd.getSecMaxAmount() * 1.0) / w.getSecondaryAmmo();

		return in;
	}

	public int incorporateLabels(int in, String[] labels) {
		// Primary ammo
		// labels[in++] = "Primary Ammo Clip Size";
		labels[in++] = "Primary Ammo Per Shot";
		labels[in++] = "Portion Primary Ammo Left";

		// Secondary ammo (not different for most weapons)
		// labels[in++] = "Secondary Ammo Clip Size";
		labels[in++] = "Secondary Ammo Per Shot";
		labels[in++] = "Portion Secondary Ammo Left";

		return in;
	}

	public int numberOfSensors() {
		return 2 * 2;
	}
}
