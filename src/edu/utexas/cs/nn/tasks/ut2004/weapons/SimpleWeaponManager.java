package edu.utexas.cs.nn.tasks.ut2004.weapons;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.WeaponPref;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

/**
 *
 * @author Jacob Schrum
 */
public class SimpleWeaponManager implements UT2004WeaponManager {

    public static int MEMORY_TIME = 30;

    public SimpleWeaponManager() {
    }

    public void prepareWeaponPreferences(UT2004BotModuleController bot) {
        bot.getWeaponPrefs().addGeneralPref(ItemType.MINIGUN, false);
        bot.getWeaponPrefs().addGeneralPref(ItemType.MINIGUN, true);
        bot.getWeaponPrefs().addGeneralPref(ItemType.LINK_GUN, false);
        bot.getWeaponPrefs().addGeneralPref(ItemType.LIGHTNING_GUN, true);
        bot.getWeaponPrefs().addGeneralPref(ItemType.SHOCK_RIFLE, true);
        bot.getWeaponPrefs().addGeneralPref(ItemType.ROCKET_LAUNCHER, true);
        bot.getWeaponPrefs().addGeneralPref(ItemType.LINK_GUN, true);
        bot.getWeaponPrefs().addGeneralPref(ItemType.ASSAULT_RIFLE, true);
        bot.getWeaponPrefs().addGeneralPref(ItemType.FLAK_CANNON, false);
        bot.getWeaponPrefs().addGeneralPref(ItemType.FLAK_CANNON, true);
        bot.getWeaponPrefs().addGeneralPref(ItemType.BIO_RIFLE, true);
    }

    public ItemType chooseWeapon(UT2004BotModuleController bot) {
        Player opponent = bot.getPlayers().getNearestEnemy(MEMORY_TIME);
        WeaponPref wp = opponent == null ? bot.getWeaponPrefs().getWeaponPreference() : bot.getWeaponPrefs().getWeaponPreference(opponent);
        return wp.getWeapon();
    }

    public UT2004WeaponManager copy() {
        return new SimpleWeaponManager();
    }
}
