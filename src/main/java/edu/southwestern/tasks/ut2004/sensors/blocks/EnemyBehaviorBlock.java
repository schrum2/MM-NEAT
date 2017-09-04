/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.ut2004.sensors.blocks;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

/**
 *
 * @author Jacob Schrum
 */
public class EnemyBehaviorBlock implements UT2004SensorBlock {

	public static final int MEMORY_TIME = 5;
	public static final double VELOCITY_EPSILON = 10;

	public void prepareBlock(UT2004BotModuleController bot) {
	}

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

	public int incorporateLabels(int in, String[] labels) {
		labels[in++] = "Opponent Firing?";
		labels[in++] = "Opponent Still?";
		labels[in++] = "Opponent Jumping?";
		return in;
	}

	public int numberOfSensors() {
		return 3;
	}
}
