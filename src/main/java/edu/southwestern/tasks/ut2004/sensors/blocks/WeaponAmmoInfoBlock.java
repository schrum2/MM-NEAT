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

	/**
	 * creates sensor block
	 */
	public void prepareBlock(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
	}

	/**
	 * Collects data on the bot's weapon status and puts it into an array
	 * 
	 * @param bot (bot which will use the sensor data)
	 * @param in (address to start at in array)
	 * @param inputs (an array that collects the values from the statuses)
	 * @return returns next address for sensor allocation
	 */
	public int incorporateSensors(@SuppressWarnings("rawtypes") UT2004BotModuleController bot, int in, double[] inputs) {
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

	/**
	 * populates the labels array so statuses can be identified
	 * 
	 * @param in (address in the array to be labeled)
	 * @param labels (an empty array that will be populated)
	 * @return returns the next address to be labeled
	 */
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

	/**
	 * @return returns the number of sensors
	 */
	public int numberOfSensors() {
		return 4;
	}
}
