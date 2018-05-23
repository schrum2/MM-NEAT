package edu.southwestern.tasks.ut2004.sensors.blocks;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;

/**
 * Collects data on the bot itself.
 * @author Jacob Schrum
 */
public class SelfAwarenessBlock implements UT2004SensorBlock {

	/**
	 * creates the sensor block
	 * @param bot (bot which will use the sensor data)
	 */
	public void prepareBlock(UT2004BotModuleController bot) {
	}

	/**
	 * Collects data on the bot's status and puts it into an array
	 * 
	 * @param bot (bot which will use the sensor data)
	 * @param in (address to start at in array)
	 * @param inputs (an array that collects the values from the statuses)
	 * @return returns next address for sensor allocation
	 */
	public int incorporateSensors(UT2004BotModuleController bot, int in, double[] inputs) {

		inputs[in++] = bot.getInfo().getArmor() / 100.0;
		inputs[in++] = bot.getInfo().getHealth() / 100.0;
		// inputs[in++] = bot.getInfo().isHealthy() ? 1 : 0;
		inputs[in++] = bot.getInfo().isTouchingGround() ? 1 : 0;
		inputs[in++] = bot.getSenses().isBeingDamaged() ? 1 : 0;
		inputs[in++] = bot.getSenses().isBumping() ? 1 : 0;
		// inputs[in++] = bot.getSenses().isBumpingPlayer() ? 1 : 0;
		inputs[in++] = bot.getSenses().isCausingDamage() ? 1 : 0;
		inputs[in++] = bot.getSenses().isColliding() ? 1 : 0;
		inputs[in++] = bot.getSenses().isFallEdge() ? 1 : 0;
		// inputs[in++] = bot.getSenses().isShot() ? 1 : 0;

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
		labels[in++] = "Armor";
		labels[in++] = "Health";
		// labels[in++] = "Healthy?";
		labels[in++] = "Touching Ground?";
		labels[in++] = "Being Damaged?";
		labels[in++] = "Bumping?";
		// labels[in++] = "Bumping Player?";
		labels[in++] = "Causing Damage?";
		labels[in++] = "Colliding?";
		labels[in++] = "Fall Edge?";
		// labels[in++] = "Shot?";

		return in;
	}

	/**
	 * @return returns the number of sensors the bot has
	 */
	public int numberOfSensors() {
		return 8;
	}
}
