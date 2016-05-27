package edu.utexas.cs.nn.tasks.ut2004.sensors.blocks;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;

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

		inputs[in++] = type.equals(ItemType.ASSAULT_RIFLE) ? 1 : 0;
		inputs[in++] = type.equals(ItemType.BIO_RIFLE) ? 1 : 0;
		inputs[in++] = type.equals(ItemType.FLAK_CANNON) ? 1 : 0;
		inputs[in++] = type.equals(ItemType.LIGHTNING_GUN) || w.getType().equals(ItemType.SNIPER_RIFLE) ? 1 : 0;
		inputs[in++] = type.equals(ItemType.LINK_GUN) ? 1 : 0;
		inputs[in++] = type.equals(ItemType.MINIGUN) ? 1 : 0;
		inputs[in++] = type.equals(ItemType.ROCKET_LAUNCHER) ? 1 : 0;
		inputs[in++] = type.equals(ItemType.SHIELD_GUN) ? 1 : 0;
		inputs[in++] = type.equals(ItemType.SHOCK_RIFLE) ? 1 : 0;

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
