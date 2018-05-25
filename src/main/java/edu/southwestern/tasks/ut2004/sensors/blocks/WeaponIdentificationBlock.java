package edu.southwestern.tasks.ut2004.sensors.blocks;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;

/**
 * Sensor block simply tells the bot which weapon it is using
 *
 * @author Jacob Schrum
 */
public class WeaponIdentificationBlock implements UT2004SensorBlock {

	/**
	 * creates the sensor block
	 */
	public void prepareBlock(UT2004BotModuleController bot) {
	}

	/**
	 * Collects data on the weapon statuses and puts it into an array
	 * 
	 * @param bot (bot which will use the sensor data)
	 * @param in (address to start at in array)
	 * @param inputs (an array that collects the values from the statuses)
	 * @return returns next address for sensor allocation
	 */
	public int incorporateSensors(UT2004BotModuleController bot, int in, double[] inputs) {
		Weaponry weaponry = bot.getWeaponry();
		Weapon w = weaponry.getCurrentWeapon();
		ItemType type = w.getType();

		inputs[in++] = type.equals(UT2004ItemType.ASSAULT_RIFLE) ? 1 : 0;
		inputs[in++] = type.equals(UT2004ItemType.BIO_RIFLE) ? 1 : 0;
		inputs[in++] = type.equals(UT2004ItemType.FLAK_CANNON) ? 1 : 0;
		inputs[in++] = type.equals(UT2004ItemType.LIGHTNING_GUN) || w.getType().equals(UT2004ItemType.SNIPER_RIFLE) ? 1 : 0;
		inputs[in++] = type.equals(UT2004ItemType.LINK_GUN) ? 1 : 0;
		inputs[in++] = type.equals(UT2004ItemType.MINIGUN) ? 1 : 0;
		inputs[in++] = type.equals(UT2004ItemType.ROCKET_LAUNCHER) ? 1 : 0;
		inputs[in++] = type.equals(UT2004ItemType.SHIELD_GUN) ? 1 : 0;
		inputs[in++] = type.equals(UT2004ItemType.SHOCK_RIFLE) ? 1 : 0;

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
		labels[in++] = "Assault Rifle Equipped?";
		labels[in++] = "Bio Rifle Equipped?";
		labels[in++] = "Flak Cannon Equipped?";
		labels[in++] = "Sniping Weapon Equipped?";
		labels[in++] = "Link Gun Equipped?";
		labels[in++] = "Minigun Equipped?";
		labels[in++] = "Rocket Launcher Equipped?";
		labels[in++] = "Shield Gun Equipped?";
		labels[in++] = "Shock Rifle Equipped?";

		return in;
	}

	/**
	 * returns the number of sensors
	 */
	public int numberOfSensors() {
		return 9;
	}
}
