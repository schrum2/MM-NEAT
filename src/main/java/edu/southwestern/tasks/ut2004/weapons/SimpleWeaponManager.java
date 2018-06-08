package edu.southwestern.tasks.ut2004.weapons;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.WeaponPref;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

/**
 * choses the bot's weapons and directs it to change to chose gun
 * @author Jacob Schrum
 */
public class SimpleWeaponManager implements UT2004WeaponManager {

	public static int MEMORY_TIME = 30;

	public SimpleWeaponManager() {
	}

	/**
	 * sets up the weapons preferences for the bot
	 * @param bot (bot being given the preferences) 
	 */
	public void prepareWeaponPreferences(UT2004BotModuleController bot) {
		//true = primary mode  false = secondary mode
		bot.getWeaponPrefs().addGeneralPref(UT2004ItemType.MINIGUN, false);
		bot.getWeaponPrefs().addGeneralPref(UT2004ItemType.MINIGUN, true);
		bot.getWeaponPrefs().addGeneralPref(UT2004ItemType.LINK_GUN, false);
		bot.getWeaponPrefs().addGeneralPref(UT2004ItemType.LIGHTNING_GUN, true);
		bot.getWeaponPrefs().addGeneralPref(UT2004ItemType.SHOCK_RIFLE, true);
		bot.getWeaponPrefs().addGeneralPref(UT2004ItemType.ROCKET_LAUNCHER, true);
		bot.getWeaponPrefs().addGeneralPref(UT2004ItemType.LINK_GUN, true);
		bot.getWeaponPrefs().addGeneralPref(UT2004ItemType.ASSAULT_RIFLE, true);
		bot.getWeaponPrefs().addGeneralPref(UT2004ItemType.FLAK_CANNON, false);
		bot.getWeaponPrefs().addGeneralPref(UT2004ItemType.FLAK_CANNON, true);
		bot.getWeaponPrefs().addGeneralPref(UT2004ItemType.BIO_RIFLE, true);
	}

	/**
	 * chooses which weapon the bot will use
	 * @param bot (bot that will use the weapon)
	 * @return returns the weapon choice 
	 */
	public ItemType chooseWeapon(UT2004BotModuleController bot) {
		Player opponent = bot.getPlayers().getNearestEnemy(MEMORY_TIME);
		WeaponPref wp = opponent == null ? bot.getWeaponPrefs().getWeaponPreference()
				: bot.getWeaponPrefs().getWeaponPreference(opponent);
		return wp.getWeapon();
	}

	/**
	 * @return returns a copy of the weapon manager
	 */
	public UT2004WeaponManager copy() {
		return new SimpleWeaponManager();
	}
}
