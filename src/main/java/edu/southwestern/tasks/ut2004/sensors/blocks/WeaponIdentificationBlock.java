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

	public void prepareBlock(UT2004BotModuleController bot) {
	}

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

	public int numberOfSensors() {
		return 9;
	}
}
