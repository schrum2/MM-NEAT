package edu.southwestern.tasks.ut2004.sensors.blocks;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import edu.southwestern.tasks.ut2004.Util;

/**
 *
 * @author Jacob Schrum
 */
public class NearestAgentDistanceBlock implements UT2004SensorBlock {

	public static final int MAX_DISTANCE = 1000;
	public static final int MEMORY_TIME = 5;
	public final boolean senseEnemy; 
	
	public NearestAgentDistanceBlock(boolean enemySetting) {
		senseEnemy = enemySetting;
	}

	public void prepareBlock(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
	}

	public int incorporateSensors(@SuppressWarnings("rawtypes") UT2004BotModuleController bot, int in, double[] inputs) {
		Player agent = senseEnemy ? 
				bot.getPlayers().getNearestEnemy(MEMORY_TIME) :
				bot.getPlayers().getNearestFriend(MEMORY_TIME);
		
		Location botLocation = bot.getInfo().getLocation();
		Location opponentLocation = agent == null ? null : agent.getLocation();
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
		labels[in++] = "Nearest " + (senseEnemy ? "Enemy" : "Friend") + "Proximity 3D";
		labels[in++] = "Nearest " + (senseEnemy ? "Enemy" : "Friend") + "Proximity 2D";
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
