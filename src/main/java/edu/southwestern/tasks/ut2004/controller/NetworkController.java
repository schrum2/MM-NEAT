package edu.utexas.cs.nn.tasks.ut2004.controller;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.ut2004.actions.BotAction;
import edu.utexas.cs.nn.tasks.ut2004.actuators.UT2004OutputInterpretation;
import edu.utexas.cs.nn.tasks.ut2004.sensors.UT2004SensorModel;
import edu.utexas.cs.nn.tasks.ut2004.weapons.UT2004WeaponManager;

/**
 *
 * @author Jacob Schrum
 */
public class NetworkController<T extends Network> extends Organism<T>implements BotController {

	Network brain;
	public final UT2004SensorModel sensorModel;
	public final UT2004OutputInterpretation outputModel;
	public final UT2004WeaponManager weaponManager;

	public NetworkController(Genotype<T> g, UT2004SensorModel sensorModel, UT2004OutputInterpretation outputModel,
			UT2004WeaponManager weaponManager) {
		super(g);
		brain = g.getPhenotype();
		this.sensorModel = sensorModel;
		this.outputModel = outputModel;
		this.weaponManager = weaponManager;
	}

	public void reset(UT2004BotModuleController bot) {
		brain.flush();
	}

	public BotAction control(UT2004BotModuleController bot) {
		// pick best weapon
		bot.getWeaponry().changeWeapon(weaponManager.chooseWeapon(bot));
		// Get sensot values
		double[] inputs = sensorModel.readSensors(bot);
		// Process through network
		double[] outputs = brain.process(inputs);
		// Interpret outputs as action
		BotAction action = outputModel.interpretOutputs(bot, outputs);

		// Testing
		// Player opponent = bot.getPlayers().getNearestEnemy(30);
		// action = new OpponentRelativeMovementAction(opponent, 1, 0, true,
		// false);

		return action;
	}

	public void initialize(UT2004BotModuleController bot) {
		sensorModel.prepareSensorModel(bot);
		weaponManager.prepareWeaponPreferences(bot);
	}
}
