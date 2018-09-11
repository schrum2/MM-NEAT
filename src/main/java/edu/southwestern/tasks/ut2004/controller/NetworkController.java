package edu.southwestern.tasks.ut2004.controller;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.evolution.Organism;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.tasks.ut2004.actions.BotAction;
import edu.southwestern.tasks.ut2004.actuators.UT2004OutputInterpretation;
import edu.southwestern.tasks.ut2004.sensors.UT2004SensorModel;
import edu.southwestern.tasks.ut2004.weapons.UT2004WeaponManager;

/**
 * Relays directions to the bot
 * @author Jacob Schrum
 */
public class NetworkController<T extends Network> extends Organism<T>implements BotController {

	Network brain;
	public final UT2004SensorModel sensorModel;
	public final UT2004OutputInterpretation outputModel;
	public final UT2004WeaponManager weaponManager;

	/**
	 * Sets up the controller for the bot
	 * 
	 * @param g (the genotype of the bot)
	 * @param sensorModel (sensors for the bot to use)
	 * @param outputModel (how the bot will dictate actions)
	 * @param weaponManager (sets the bot's weapon preferences)
	 */
	public NetworkController(Genotype<T> g, UT2004SensorModel sensorModel, UT2004OutputInterpretation outputModel,
			UT2004WeaponManager weaponManager) {
		super(g);
		brain = g.getPhenotype();
		this.sensorModel = sensorModel;
		this.outputModel = outputModel;
		this.weaponManager = weaponManager;
	}

	/**
	 * resets the controller to be reprogrammed
	 */
	public void reset(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		brain.flush();
	}

	/**
	 * reads data from the inputs, and interprets it into actions
	 * @return returns the next action for the bot to take
	 */
	public BotAction control(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
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

	/**
	 * creates the module controller
	 */
	public void initialize(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		sensorModel.prepareSensorModel(bot);
		weaponManager.prepareWeaponPreferences(bot);
	}

	@Override
	public String getSkin() {
		return "HumanFemaleA.MercFemaleA";
	}
}
