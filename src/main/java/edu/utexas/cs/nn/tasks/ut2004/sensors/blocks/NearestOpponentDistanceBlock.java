package edu.utexas.cs.nn.tasks.ut2004.sensors.blocks;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import edu.utexas.cs.nn.tasks.ut2004.Util;

/**
 *
 * @author Jacob Schrum
 */
public class NearestOpponentDistanceBlock implements UT2004SensorBlock {

	public static final int MAX_DISTANCE = 1000;
	public static final int MEMORY_TIME = 5;

	public void prepareBlock(UT2004BotModuleController bot) {
	}

	public int incorporateSensors(UT2004BotModuleController bot, int in, double[] inputs) {
		Player opponent = bot.getPlayers().getNearestEnemy(MEMORY_TIME);
		Location botLocation = bot.getInfo().getLocation();
		Location opponentLocation = opponent == null ? null : opponent.getLocation();
		double distance = (botLocation == null || opponentLocation == null) ? MAX_DISTANCE
				: botLocation.getDistance(opponentLocation);
		double distance2D = (botLocation == null || opponentLocation == null) ? MAX_DISTANCE
				: botLocation.getDistance2D(opponentLocation);
		distance = Math.min(distance, MAX_DISTANCE);
		distance2D = Math.min(distance2D, MAX_DISTANCE);

		inputs[in++] = Util.scale(distance, MAX_DISTANCE);
		inputs[in++] = Util.scale(distance2D, MAX_DISTANCE);

		return in;
	}

	public int incorporateLabels(int in, String[] labels) {
		labels[in++] = "Nearest Opponent Proximity 3D";
		labels[in++] = "Nearest Opponent Proximity 2D";
		return in;
	}

	/**
	 * Direct distance and distance in plane
	 *
	 * @return
	 */
	public int numberOfSensors() {
		return 2;
	}
}
