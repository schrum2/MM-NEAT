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
 * important, and some may even give weird/erroneous data.
 *
 * @author Jacob Schrum
 */
public class WeaponDamageInfoBlock implements UT2004SensorBlock {

	public void prepareBlock(UT2004BotModuleController bot) {
	}

	public int incorporateSensors(UT2004BotModuleController bot, int in, double[] inputs) {
		Weaponry weaponry = bot.getWeaponry();
		Weapon w = weaponry.getCurrentWeapon();
		WeaponDescriptor wd = w.getDescriptor();

		// General properties
		inputs[in++] = wd.isMelee() ? 1 : 0;
		inputs[in++] = wd.isSniping() ? 1 : 0;

		// For primary fire mode
		// inputs[in++] = wd.getPriAimError();
		inputs[in++] = wd.getPriBotRefireRate();
		inputs[in++] = wd.getPriDamage() / 100.0;
		// inputs[in++] = wd.getPriDamageAtten();
		// inputs[in++] = wd.getPriDamageRadius();
		// inputs[in++] = wd.getPriFireRate();
		// inputs[in++] = wd.getPriLifeSpan();
		// inputs[in++] = wd.getPriMaxEffectDistance();
		// inputs[in++] = wd.getPriMaxRange();
		// inputs[in++] = wd.getPriMaxSpeed();
		// inputs[in++] = wd.getPriSpeed();
		inputs[in++] = wd.getPriSpread();
		// inputs[in++] = wd.getPriTossZ();
		// inputs[in++] = wd.isPriExtraMomZ() ? 1 : 0;
		inputs[in++] = wd.isPriFireOnRelease() ? 1 : 0;
		inputs[in++] = wd.isPriInstantHit() ? 1 : 0;
		// inputs[in++] = wd.isPriLeadTarget() ? 1 : 0;
		inputs[in++] = wd.isPriSplashDamage() ? 1 : 0;
		inputs[in++] = wd.isPriTossed() ? 1 : 0;

		// For secondary fire mode
		// inputs[in++] = wd.getSecAimError();
		inputs[in++] = wd.getSecBotRefireRate();
		inputs[in++] = wd.getSecDamage() / 100.0;
		// inputs[in++] = wd.getSecDamageAtten();
		// inputs[in++] = wd.getSecDamageRadius();
		// inputs[in++] = wd.getSecFireRate();
		// inputs[in++] = wd.getSecLifeSpan();
		// inputs[in++] = wd.getSecMaxEffectDistance();
		// inputs[in++] = wd.getSecMaxRange();
		// inputs[in++] = wd.getSecMaxSpeed();
		// inputs[in++] = wd.getSecSpeed();
		inputs[in++] = wd.getSecSpread();
		// inputs[in++] = wd.getSecTossZ();
		// inputs[in++] = wd.isSecExtraMomZ() ? 1 : 0;
		inputs[in++] = wd.isSecFireOnRelease() ? 1 : 0;
		inputs[in++] = wd.isSecInstantHit() ? 1 : 0;
		// inputs[in++] = wd.isSecLeadTarget() ? 1 : 0;
		inputs[in++] = wd.isSecSplashDamage() ? 1 : 0;
		inputs[in++] = wd.isSecTossed() ? 1 : 0;

		return in;
	}

	public int incorporateLabels(int in, String[] labels) {
		// General properties
		labels[in++] = "Melee Weapon?";
		labels[in++] = "Sniping Weapon?";

		// For primary fire mode
		// labels[in++] = "Primary Aiming Error";
		labels[in++] = "Primary Refire Rate";
		labels[in++] = "Primary Damage";
		// labels[in++] = "Primary Damage Attenuation";
		// labels[in++] = "Primary Damage Radius";
		// labels[in++] = "Primary Fire Rate";
		// labels[in++] = "Primary Projectile Lifespan";
		// labels[in++] = "Primary Max Distance";
		// labels[in++] = "Primary Max Range";
		// labels[in++] = "Primary Max Speed";
		// labels[in++] = "Primary Speed";
		labels[in++] = "Primary Spread";
		// labels[in++] = "Primary Toss Z";
		// labels[in++] = "Primary Pushes?";
		labels[in++] = "Primary Fire On Release?";
		labels[in++] = "Primary Instant Hit?";
		// labels[in++] = "Primary Lead Target?";
		labels[in++] = "Primary Splash Damage?";
		labels[in++] = "Primary Tossed?";

		// For secondary fire mode
		// labels[in++] = "Secondary Aiming Error";
		labels[in++] = "Secondary Refire Rate";
		labels[in++] = "Secondary Damage";
		// labels[in++] = "Secondary Damage Attenuation";
		// labels[in++] = "Secondary Damage Radius";
		// labels[in++] = "Secondary Fire Rate";
		// labels[in++] = "Secondary Projectile Lifespan";
		// labels[in++] = "Secondary Max Distance";
		// labels[in++] = "Secondary Max Range";
		// labels[in++] = "Secondary Max Speed";
		// labels[in++] = "Secondary Speed";
		labels[in++] = "Secondary Spread";
		// labels[in++] = "Secondary Toss Z";
		// labels[in++] = "Secondary Pushes?";
		labels[in++] = "Secondary Fire On Release?";
		labels[in++] = "Secondary Instant Hit?";
		// labels[in++] = "Secondary Lead Target?";
		labels[in++] = "Secondary Splash Damage?";
		labels[in++] = "Secondary Tossed?";

		return in;
	}

	public int numberOfSensors() {
		return 2 + (2 * 7);
	}
}
