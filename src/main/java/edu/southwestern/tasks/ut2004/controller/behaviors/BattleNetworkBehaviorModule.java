/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.ut2004.controller.behaviors;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.tasks.ut2004.actuators.UT2004OutputInterpretation;
import edu.southwestern.tasks.ut2004.controller.NetworkController;
import edu.southwestern.tasks.ut2004.sensors.UT2004SensorModel;
import edu.southwestern.tasks.ut2004.weapons.UT2004WeaponManager;

/**
 *
 * @author Jacob Schrum
 */
public class BattleNetworkBehaviorModule<T extends Network> extends NetworkController<T>implements BehaviorModule {

	public BattleNetworkBehaviorModule(NetworkController<T> nc) {
		this(nc.getGenotype(), nc.sensorModel, nc.outputModel, nc.weaponManager);
	}

	public BattleNetworkBehaviorModule(Genotype<T> g, UT2004SensorModel sensorModel,
			UT2004OutputInterpretation outputModel, UT2004WeaponManager weaponManager) {
		super(g, sensorModel, outputModel, weaponManager);
	}

	public boolean trigger(UT2004BotModuleController bot) {
		return bot.getPlayers().canSeeEnemies() && bot.getWeaponry().hasLoadedRangedWeapon();
	}
}
