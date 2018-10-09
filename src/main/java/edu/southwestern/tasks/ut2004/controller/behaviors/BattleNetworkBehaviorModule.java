package edu.southwestern.tasks.ut2004.controller.behaviors;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.tasks.ut2004.actuators.UT2004OutputInterpretation;
import edu.southwestern.tasks.ut2004.controller.NetworkController;
import edu.southwestern.tasks.ut2004.sensors.UT2004SensorModel;
import edu.southwestern.tasks.ut2004.weapons.UT2004WeaponManager;

/**
 * Uses data from the network controller to create actions
 * @author Jacob Schrum
 */
public class BattleNetworkBehaviorModule<T extends Network> extends NetworkController<T>implements BehaviorModule {

	/**
	 * retireves the genotype, sensorModel, outputModel, and weaponManager from the network controller
	 * @param nc (a given network controller)
	 */
	public BattleNetworkBehaviorModule(NetworkController<T> nc) {
		this(nc.getGenotype(), nc.sensorModel, nc.outputModel, nc.weaponManager);
	}

	/**
	 * sets the genotype, sensorModel, outputModel, and weaponManager for the controller from given parameters
	 * 
	 * @param g (genotypes for the bot)
	 * @param sensorModel (sensor model the bot will use)
	 * @param outputModel (output model to be used)
	 * @param weaponManager (weapon preferences)
	 */
	public BattleNetworkBehaviorModule(Genotype<T> g, UT2004SensorModel sensorModel,
			UT2004OutputInterpretation outputModel, UT2004WeaponManager weaponManager) {
		super(g, sensorModel, outputModel, weaponManager);
	}

	/**
	 * tells the bot whether to follow this behaviour
	 */
	public boolean trigger(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		return (bot.getPlayers().canSeePlayers() && bot.getWeaponry().hasLoadedRangedWeapon());
		//neural network will now trigger if the bot sees friends or enemies
	}
}
