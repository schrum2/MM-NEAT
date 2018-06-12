package edu.southwestern.tasks.ut2004.sensors.blocks;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import edu.southwestern.tasks.ut2004.Util;
import java.util.Map;

/**
 * Tells the bot how close enemies are to it based on pie slices of the areas around it
 * @author Jacob Schrum
 */
public class PieSliceOpponentSensorBlock implements UT2004SensorBlock {

	public static int MAX_DISTANCE = 1000;
	public double[] sliceLimits = new double[] { 0, Math.PI / 128, Math.PI / 32, Math.PI / 4, Math.PI / 2, Math.PI };

	/**
	 * creates the sensor block
	 * @param bot (bot which will use the sensor data)
	 */
	public void prepareBlock(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
	}

	/**
	 * Builds the sensor value array
	 * @param bot (bot which will use the sensor data)
	 * @param in (address to start at in array)
	 * @param inputs (an array that collects the values from the statuses)
	 * @return returns next address for sensor allocation
	 */
	public int incorporateSensors(@SuppressWarnings("rawtypes") UT2004BotModuleController bot, int in, double[] inputs) {
		// Gather data in slices
		double[] slices = new double[numberOfSlices()];
		Map<UnrealId, Player> seenPlayers = bot.getPlayers().getVisibleEnemies();
		double frontLeftDist = 0;
		double frontRightDist = 0;
		@SuppressWarnings("unused")	int numPlayers = 0;
		Location botLocation = bot.getInfo().getLocation();
		for (Player seenPlayer : seenPlayers.values()) {
			Location playerLocation = seenPlayer.getLocation();
			if (playerLocation == null || botLocation == null) {
				continue;
			}

			double distance = Util.scale(botLocation.getDistance(playerLocation), MAX_DISTANCE);
			numPlayers++;
			double angle = Util.relativeAngleToTarget(botLocation, bot.getInfo().getHorizontalRotation(),
					playerLocation);
			if (angle > 0) {
				for (int i = 0; i < sliceLimits.length - 1; i++) {
					if (sliceLimits[i] < angle && angle <= sliceLimits[i + 1]) {
						slices[i]++;
						if (i == 0) {
							frontLeftDist = distance;
						}
						break;
					}
				}
			} else {
				for (int i = 0; i < sliceLimits.length - 1; i++) {
					if (-sliceLimits[i + 1] < angle && angle <= -sliceLimits[i]) {
						slices[i + sliceLimits.length - 1]++;
						if (i == 0) {
							frontRightDist = distance;
						}
						break;
					}
				}
			}
		}

		// Now put data into inputs
		for (int i = 0; i < slices.length; i++) {
			inputs[in++] = slices[i];
		}
		inputs[in++] = frontLeftDist;
		inputs[in++] = frontRightDist;

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
		for (int i = 1; i < sliceLimits.length; i++) {
			labels[in++] = "Left Slice " + i;
		}
		for (int i = 1; i < sliceLimits.length; i++) {
			labels[in++] = "Right Slice " + i;
		}
		labels[in++] = "Front Left Distance";
		labels[in++] = "Front Right Distance";
		return in;
	}

	public int numberOfSlices() {
		return ((sliceLimits.length - 1) * 2);
	}

	/**
	 * Pie slices plus left and right distances
	 *
	 * @return returns the number of pie slices
	 */
	public int numberOfSensors() {
		return numberOfSlices() + 2;
	}
}
