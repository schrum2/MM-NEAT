package edu.southwestern.tasks.ut2004.sensors.blocks;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

/**
 * Monitors the behaviour of an enemy
 * @author Jacob Schrum
 */
public class EnemyBehaviorBlock implements UT2004SensorBlock {

	public static final int MEMORY_TIME = 5;
	public static final double VELOCITY_EPSILON = 10;

	/**
	 * creates the sensor block
	 * @param bot (bot which will use the sensor data)
	 */
	public void prepareBlock(UT2004BotModuleController bot) {
	}

	/**
	 * @param bot (bot which will use the sensor data)
	 * @param in (address to start at in array)
	 * @param inputs (an array that collects the values from the statuses)
	 * @return returns next address for sensor allocation
	 */
	public int incorporateSensors(UT2004BotModuleController bot, int in, double[] inputs) {
		Player opponent = bot.getPlayers().getNearestEnemy(MEMORY_TIME);

		inputs[in++] = opponent != null && opponent.getFiring() > 0 ? 1 : 0;
		inputs[in++] = isStill(opponent) ? 1 : 0;
		inputs[in++] = isJumping(opponent) ? 1 : 0;

		return in;
	}

	public static boolean isStill(Player p) {
		return p != null && p.getVelocity() != null ? p.getVelocity().isZero(VELOCITY_EPSILON) : false;
	}

	public static boolean isJumping(Player p) {
		return p != null && p.getVelocity() != null ? Math.abs(p.getVelocity().z) > 50 : false; // Magic
																								// number!!!
	}

	/**
	 * populates the labels array so statuses can be identified
	 * 
	 * @param in (address in the array to be labeled)
	 * @param labels (an empty array that will be populated)
	 * @return returns the next address to be labeled
	 */
	public int incorporateLabels(int in, String[] labels) {
		labels[in++] = "Opponent Firing?";
		labels[in++] = "Opponent Still?";
		labels[in++] = "Opponent Jumping?";
		return in;
	}

	/**
	 * @return returns the number of sensors in this block
	 */
	public int numberOfSensors() {
		return 3;
	}
}
