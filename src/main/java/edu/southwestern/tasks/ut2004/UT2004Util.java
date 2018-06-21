package edu.southwestern.tasks.ut2004;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import edu.southwestern.parameters.Parameters;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.vecmath.Vector3d;

/**
 * These are all util methods for UT2004, but I think some
 * if not all of them should be moved to other, more general
 * util classes. Most seem geometric. Some may even duplicate 
 * existing methods.
 * 
 * @author Jacob Schrum
 */
public class UT2004Util {
	//info found at https://wiki.beyondunreal.com/Legacy:General_Scale_And_Dimensions
	public static final int NUMBER_OF_UNREAL_UNITS_IN_A_FULL_CIRCLE = 65536;

	/**
	 * Designed for scaling down input sensor values to a common range
	 *
	 * @param distance
	 *            actual distance
	 * @param max
	 *            distance to treat as the max
	 * @return scaled value in [0,1]
	 */
	public static double scale(double distance, double max) {
		return Math.exp(-distance / max);
	}

	/**
	 * Returns the angle in radians to a target (in the horizontal plane)
	 *
	 * @param agentLocation
	 *            the location of the source that should be measured from
	 * @param agentRotation
	 *            the current rotation of the source
	 * @param targetLocation
	 *            the location of your target
	 * @return the angle (in the horizontal plane) between -PI and PI the the bot
	 *         should turn to face the target (positive if the target is left
	 *         from the agent, negative if the target is right from the agent)
	 */
	public static double relativeAngleToTarget(Location agentLocation, Rotation agentRotation,
			Location targetLocation) {
		Location vectorToTarget = targetLocation.sub(agentLocation);
		Vector3d rotationVector = rotationAsVectorUTUnits(rotationToVector(agentRotation));
		double angle = Math.atan2(rotationVector.getY(), rotationVector.getX())
				- Math.atan2(vectorToTarget.y, vectorToTarget.x);
		angle = angle > Math.PI ? (-2 * Math.PI) + angle : angle;
		angle = angle < -Math.PI ? (2 * Math.PI) + angle : angle;
		return angle;
	}

	/**
	 * converts the rotation to a vector
	 * @param r (the bot's rotation)
	 * @return returns the rotation as a vector
	 */
	public static Vector3d rotationToVector(Rotation r) {
		return (r == null ? null : new Vector3d(r.pitch, r.yaw, r.roll));
	}

        // Pretty complicated. I can't remember where the math for this came from.
	/**
	 * adjusts the yaw, pitch, and roll of the vector
	 * @param a (the vector)
	 * @param yawRad (the vector's yaw)
	 * @param pitchRad (the vector's pitch)
	 * @param rollRad (the vector's roll)
	 * @return returns the vector
	 */
	public static Vector3d rotateYawPitchRoll(Vector3d a, double yawRad, double pitchRad, double rollRad) {
		return new Vector3d(
				  (Math.cos(pitchRad) * Math.cos(yawRad))  * a.getX() 
				+ (Math.sin(rollRad)  * Math.sin(pitchRad) * Math.cos(yawRad) - Math.cos(rollRad) * Math.sin(yawRad)) * a.getY()
				+ (Math.cos(rollRad)  * Math.sin(pitchRad) * Math.cos(yawRad) + Math.sin(rollRad) * Math.sin(yawRad)) * a.getZ(),
				  (Math.cos(pitchRad) * Math.sin(yawRad))  * a.getX()
				+ (Math.sin(rollRad)  * Math.sin(pitchRad) * Math.sin(yawRad) + Math.cos(rollRad) * Math.cos(yawRad)) * a.getY()
				+ (Math.cos(rollRad)  * Math.sin(pitchRad) * Math.sin(yawRad) - Math.sin(rollRad) * Math.cos(yawRad)) * a.getZ(),
				  (-Math.sin(pitchRad)) * a.getX() 
                                + (Math.cos(pitchRad)   * Math.sin(rollRad)) * a.getY() 
                                + (Math.cos(pitchRad)   * Math.cos(rollRad)) * a.getZ());
	}

	/**
	 * @param rotationInRad
	 *            in radians pitch, yaw, roll (same order as sent by GameBots)
	 * @return
	 */
	public static Vector3d rotationAsVector(Vector3d rotationInRad) {
		return rotateYawPitchRoll(new Vector3d(1, 0, 0), rotationInRad.getY(), rotationInRad.getX(), rotationInRad.getX());
	}

	/**
	 * Checks if the given value is between -delta and delta
	 *
	 * @param value
	 *            the value to check
	 * @param delta
	 *            the range to check
	 * @return true if value is between -delta and delta, false otherwise
	 */
	public static boolean isBetween(double value, double delta) {
		return isBetween(value, -delta, delta);
	}

	/**
	 * Checks if a value is between two given values
	 *
	 * @param value
	 *            the value to check
	 * @param lowValue
	 *            value should be higher than this
	 * @param highValue
	 *            value should be lower than this
	 * @return true if value is higher than lowValue and lower than highvalue,
	 *         false otherwise
	 */
	public static boolean isBetween(double value, double lowValue, double highValue) {
		return (lowValue < value && value < highValue);
	}

	/**
	 * 
	 * @param rotationInUTUnits (in UTUnits (0..65535) pitch, yaw, roll (same order as sent by GameBots)) 
	 * @return returns the rotatin as a vector
	 */
	public static Vector3d rotationAsVectorUTUnits(Rotation rotationInUTUnits) {
		return rotationAsVectorUTUnits(rotationToVector(rotationInUTUnits));
	}

	/**
	 * @param rotationInUTUnits
	 *            in UTUnits (0..65535) pitch, yaw, roll (same order as sent by
	 *            GameBots)
	 * @return returns the vector in UT units
	 */
	public static Vector3d rotationAsVectorUTUnits(Vector3d rotationInUTUnits) {
		return rotateYawPitchRoll(new Vector3d(1, 0, 0), utAngleToRad(rotationInUTUnits.getY()),
				utAngleToRad(rotationInUTUnits.getX()), utAngleToRad(rotationInUTUnits.getZ()));
	}

	/**
	 * converts angle to radians
	 * @param angle (angle to be converted)
	 * @return returns the converted angle
	 */
	public static double utAngleToRad(double angle) {
		return angle / NUMBER_OF_UNREAL_UNITS_IN_A_FULL_CIRCLE * 2 * Math.PI;
	}

	/**
	 * Rotate x and y coords of vector in xy-plane using standard rotation
	 * matrix
	 *
	 * @param v
	 *            initial vector
	 * @param radians
	 *            how much to rotate
	 * @return rotated vector
	 */
	public static Vector3d rotateVectorInPlane(Vector3d v, double radians) {
		double x = v.getX();
		double y = v.getY();
		double newX = (x * Math.cos(radians)) - (y * Math.sin(radians));
		double newY = (x * Math.sin(radians)) + (y * Math.cos(radians));

		return new Vector3d(newX, newY, v.getZ());
	}
	
	/**
	 * Copy the version of GameBots stored in MM-NEAT into the UT2004\System directory to make sure that
	 * the botprize mod is available.
	 * @throws IOException 
	 */
	public static void copyBotPrizeVersionOfGameBots() throws IOException {
		String[] filesToCopy = new String[] {
				"GameBots2004.u",	// 2012 version of BotPrize mod
				"GameBots2004.ucl", // 2012 version of BotPrize mod
				"GameBots2004.ini"	// Configuration that disables all GameBots visualization
		};
		String utSystemDir = Parameters.parameters.stringParameter("utDrive") + ":" + File.separator + Parameters.parameters.stringParameter("utPath") + File.separator + "System";
		for(String file :  filesToCopy) {
			Files.copy(Paths.get("data" + File.separatorChar + "unreal" + File.separatorChar + "BotPrizeGameBots"  + File.separatorChar + file), 
					   Paths.get(utSystemDir + File.separatorChar + file), 
					   REPLACE_EXISTING);
		}
	}
	
	/**
	 * Copy the version of GameBots stored in MM-NEAT into the UT2004\System directory to make sure that
	 * the botprize mod is not turned on.
	 * @throws IOException 
	 */
	public static void copyDefaultVersionOfGameBots() throws IOException {
		String[] filesToCopy = new String[] {
				"GameBots2004.u",	// 2012 version of BotPrize mod
				"GameBots2004.ucl", // 2012 version of BotPrize mod
				"GameBots2004.ini"	// Configuration that disables all GameBots visualization
		};
		String utSystemDir = Parameters.parameters.stringParameter("utDrive") + ":" + File.separator + Parameters.parameters.stringParameter("utPath") + File.separator + "System";
		for(String file :  filesToCopy) {
			Files.copy(Paths.get("data" + File.separatorChar + "unreal" + File.separatorChar + "DefaultPrizeGameBots"  + File.separatorChar + file), 
					   Paths.get(utSystemDir + File.separatorChar + file), 
					   REPLACE_EXISTING);
		}
	}
}
