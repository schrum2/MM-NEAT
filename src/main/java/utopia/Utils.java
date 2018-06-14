package utopia;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import java.util.Collection;
import java.util.Random;
import mockcz.cuni.pogamut.MessageObjects.Triple;

/**
 * Class to store static utils functions in
 *
 * @author Niels van Hoorn
 */
public class Utils {

    public static Random myRandom = new Random();
    public static int controllerCount = 0;
    public static int agentCount = 0;
    public static int removedControllerCount = 0;
    public static int removedAgentCount = 0;

    /**
     * Checks if the given value is between -delta and delta
     *
     * @param value the value to check
     * @param delta the range to check
     * @return true if value is between -delta and delta, false otherwise
     */
    public static boolean isBetween(double value, double delta) {
        return isBetween(value, -delta, delta);
    }

    /**
     * Checks if a value is between two given values
     *
     * @param value     the value to check
     * @param lowValue  value should be higher than this
     * @param highValue value should be lower than this
     * @return true if value is higher than lowValue and lower than highvalue, false otherwise
     */
    public static boolean isBetween(double value, double lowValue, double highValue) {
        return (lowValue < value && value < highValue);
    }

    /**
     * Limits the value beween -delta and delta
     *
     * @param value value to limit
     * @param delta range to limit between
     * @return the limited value
     */
    public static double limitBetween(double value, double delta) {
        return limitBetween(value, -delta, delta);
    }

    /**
     * Limits a value between two given values
     *
     * @param value        the value to limit
     * @param lowestValue  if value is less than this, it is set to this value
     * @param highestValue if value is more than this, it is set to this value
     * @return the limited value
     */
    public static double limitBetween(double value, double lowestValue, double highestValue) {
        if (lowestValue > highestValue) {
            return value;
        }
        return Math.max(lowestValue, Math.min(highestValue, value));
    }

    /**
     * Returns the angle in radians to a target (in the horizontal plane)
     *
     * @param agentLocation  the location of the source that should be measured from
     * @param agentRotation  the current rotation of the source
     * @param targetLocation the location of your target
     * @return the angle (in the horizontal plane) beween -PI and PI the the bot should turn to face the target (positive if the target is left from the agent, negative if the target is right from the agent)
     */
    public static double relativeAngleToTarget(Triple agentLocation, Triple agentRotation, Triple targetLocation) {
        Triple vectorToTarget = Triple.subtract(targetLocation, agentLocation);
        Triple rotationVector = Triple.rotationAsVectorUTUnits(agentRotation);
        double angle = Math.atan2(rotationVector.y, rotationVector.x) - Math.atan2(vectorToTarget.y, vectorToTarget.x);
        angle = angle > Math.PI ? (-2 * Math.PI) + angle : angle;
        angle = angle < -Math.PI ? (2 * Math.PI) + angle : angle;
        return angle;
    }

    public static double relativeAngleToTarget(Location agentLocation, Rotation agentRotation, Location targetLocation) {
        return relativeAngleToTarget(Triple.locationToTriple(agentLocation), Triple.rotationToTriple(agentRotation), Triple.locationToTriple(targetLocation));
    }

    /*
     *
     *
     */
    public static boolean insideCube(Location point, Location lowerLeft, Location upperRight) {
        return ((lowerLeft.x < upperRight.x && lowerLeft.x < point.x && point.x < upperRight.x || lowerLeft.x > upperRight.x && lowerLeft.x > point.x && point.x > upperRight.x) && (lowerLeft.y < upperRight.y && lowerLeft.y < point.y && point.y < upperRight.y || lowerLeft.y > upperRight.y && lowerLeft.y > point.y && point.y > upperRight.y) && (lowerLeft.z < upperRight.z && lowerLeft.z < point.z && point.z < upperRight.z || lowerLeft.z > upperRight.z && lowerLeft.z > point.z && point.z > upperRight.z));
    }

    public static double sumArray(double[] array) {
        double sum = 0;
        for (double value : array) {
            sum += value;
        }
        return sum;
    }

    public static double[] sumArrays(double[] array1, double[] array2) {
        if (array1.length != array2.length) {
            throw new IllegalArgumentException("Arrays not of the same size");
        }
        double[] result = new double[array1.length];
        for (int i = 0; i < array1.length; i++) {
            result[i] = array1[i] + array2[i];
        }
        return result;
    }

    public static void printArray(double[] ds) {
        if (ds.length == 0) {
            System.out.print("[ ]");
            return;
        }
        System.out.print("[" + ds[0]);
        for (int i = 1; i < ds.length; i++) {
            System.out.printf(", %.3f", ds[i]);
        }
        System.out.print("]");
    }

    public static void printArray(double[][] ds) {
        if (ds.length == 0) {
            System.out.print("{ }");
            return;
        }
        System.out.print("{");
        printArray(ds[0]);
        for (int i = 1; i < ds.length; i++) {
            System.out.print(",");
            printArray(ds[i]);
        }
        System.out.print("}");
    }

    public static double randomCauchy(double wtrange) {
        double u = 0.5, Cauchy_cut = 10.0;
        while (u == 0.5) {
            u = Math.random();
        }
        u = wtrange * Math.tan(u * Math.PI);
        if (Math.abs(u) > Cauchy_cut) {
            return randomCauchy(wtrange);
        } else {
            return u;
        }
    }

    public static double randomFloat() {
        return myRandom.nextDouble();
    }

    public static int randomInt(int x, int y) {
        int n = myRandom.nextInt(y - x + 1);
        return (n + x);
    }

    public static int randposneg() {

        int n = myRandom.nextInt();
        if ((n % 2) == 0) {
            return -1;
        } else {
            return 1;
        }
    }

    public static boolean randomBool(){
        return myRandom.nextBoolean();
    }
    
    /**
     * Returns the farthest object from 'target'.
     * <p><p>
     * WARNING: O(n) complexity!
     *
     * @param <T>
     * @param locations
     * @param target
     * @return farthest object from collection of objects
     */
    public static <T extends ILocated> T getFarthest(Collection<T> locations, ILocated target) {
        T farthest = null;
        Location targetLoc = target.getLocation();
        if (targetLoc == null) return null;
        double maxDistance = Double.MIN_VALUE;
        double d;
        for(T l : locations) {
        	if (l.getLocation() == null) continue;
            d = l.getLocation().getDistance(targetLoc);
            if(d > maxDistance) {
                maxDistance = d;
                farthest = l;
            }
        }
        return farthest;
    }    
}
