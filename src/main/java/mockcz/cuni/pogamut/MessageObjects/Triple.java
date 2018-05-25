package mockcz.cuni.pogamut.MessageObjects;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import java.io.Serializable;
import javax.vecmath.Vector3d;

/**
 * Triple implemets triple of double - used in representation of location, rotation, velocity
 * <br>
 * Triple has couple handy methods like add, substract, distance (in 2D and in 3D) which
 * are usefull while designing agent in complex virtual environment
 * 
 * @author Horatko
 *
 */
public class Triple implements Cloneable, Serializable, ILocated {

    public static final double EPSILON = 0.000000001;

    public double x = 0;
    public double y = 0;
    public double z = 0;
    private final int hashCode;

    public Triple(double[] triple) {
        this.x = triple[0];
        this.y = triple[1];
        this.z = triple[2];
        hashCode = getHashCode();
    }

    private int getHashCode() {
        HashCode hc = new HashCode(); // creating new HashCode instance
        hc.add(x);      // adding first parametr to hash code
        hc.add(y);     // second...
        hc.add(z);         // third...
        return hc.getHash();          // returning the hash
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public Triple() {
        x = 0.0;
        y = 0.0;
        z = 0.0;
        hashCode = getHashCode();
    }

    public Triple(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        hashCode = getHashCode();
    }

    public Triple(Triple t) {
        this.x = t.x;
        this.y = t.y;
        this.z = t.z;
        hashCode = getHashCode();
    }

    @Override
    public Object clone() {
        return new Triple(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Triple)) {
            return false;
        }
        Triple sec = (Triple) obj;
        if (sec.x == this.x && sec.y == this.y && sec.z == this.z) {
            return true;
        }
        return false;
    }

    /**
     * Dot product.
     * @param t
     * @return
     */
    public Double dot(final Triple t) {
        return x * t.x + y * t.y + z * t.z;
    }

    @Override
    public String toString() {
        String temp;
        temp = String.valueOf(this.x) + ',' + String.valueOf(this.y) + ',' + String.valueOf(this.z);
        return temp;
    }

    public void setTriple(Triple newTriple) {
        this.x = newTriple.x;
        this.y = newTriple.y;
        this.z = newTriple.z;
    }
    // OPERATORS //

    /** makes addition, example call, we got triples x, y: z = x + y can
     * be written like:
     * 	z = add(x,y);
     * or
     * 	z = add(y,x);
     */
    public static Triple add(Triple a, Triple b) {
        Triple sum = new Triple();
        sum.x = a.x + b.x;
        sum.y = a.y + b.y;
        sum.z = a.z + b.z;
        return sum;
    }

    /**
     * Normalize the vector.
     */
    public Triple normalize() {
        return multiplyByNumber(this, 1 / vectorSize());
    }

    /** makes subraction
     * 	example call: z = x - y => z = subtract(x,y);
     * @param a
     * @param b
     * @return substraction of supplied vectors
     */
    public static Triple subtract(Triple a, Triple b) {
        Triple difference = new Triple();
        difference.x = a.x - b.x;
        difference.y = a.y - b.y;
        difference.z = a.z - b.z;
        return difference;
    }

    /**
     *  multiple vector by a number - every coordination separately
     *
     */
    public static Triple multiplyByNumber(Triple a, Double b) {
        return new Triple(a.x * b, a.y * b, a.z * b);
    }

    public Triple multiplyByNumber(Double b) {
        return multiplyByNumber(this, b);
    }

    /**
     * Computes Euclidean distance in 3D space
     *
     * @param a
     * @param b
     * @return distance in space between supplied points
     */
    public static double distanceInSpace(Triple a, Triple b) {
        double distance = 0;
        distance = Math.pow((a.x - b.x), (double) 2) + Math.pow((a.y - b.y), (double) 2) + Math.pow((a.z - b.z), (double) 2);
        distance = Math.sqrt(distance);
        return distance;
    }

    public static double distanceInSpace(Location a, Location b) {
        return distanceInPlane(locationToTriple(a), locationToTriple(b));
    }

    public static double distanceInSpace(Triple a, Location b) {
        return distanceInPlane(a, locationToTriple(b));
    }

    /**
     * Computes Euclidean distance in 2D space, according to x and y aces
     *
     * @param a
     * @param b
     * @return distance in plane between supplied points
     */
    public static double distanceInPlane(Triple a, Triple b) {
        double distance = 0;
        distance = Math.pow((a.x - b.x), (double) 2) + Math.pow((a.y - b.y), (double) 2);
        distance = Math.sqrt(distance);
        return distance;

    }

    /**
     * counts size of the vector - distance from the (0,0,0)
     *
     * @param a - vector
     * @return size
     */
    public static double vectorSize(Triple a) {
        double distance = 0;
        //distance = Math.pow((a.x),(double)2) + Math.pow((a.y),(double)2) + Math.pow((a.z),(double)2);
        distance = a.x * a.x + a.y * a.y + a.z * a.z;
        distance = Math.sqrt(distance);
        return distance;
    }

    public double vectorSize() {
        return vectorSize(this);
    }

    public boolean zero() {
        if (this.x == 0 && this.y == 0 && this.z == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static double utAngleToRad(double angle) {
        return angle / 65536 * 2 * Math.PI;
    }

    public static double radToUTAngle(double angle) {
        return angle / (2 * Math.PI) * 65536;
    }

    public static Triple rotateYawPitchRoll(Triple a, double yawRad, double pitchRad, double rollRad) {

        return new Triple(
                (Math.cos(pitchRad) * Math.cos(yawRad)) * a.x
                + (Math.sin(rollRad) * Math.sin(pitchRad) * Math.cos(yawRad) - Math.cos(rollRad) * Math.sin(yawRad)) * a.y
                + (Math.cos(rollRad) * Math.sin(pitchRad) * Math.cos(yawRad) + Math.sin(rollRad) * Math.sin(yawRad)) * a.z,
                (Math.cos(pitchRad) * Math.sin(yawRad)) * a.x
                + (Math.sin(rollRad) * Math.sin(pitchRad) * Math.sin(yawRad) + Math.cos(rollRad) * Math.cos(yawRad)) * a.y
                + (Math.cos(rollRad) * Math.sin(pitchRad) * Math.sin(yawRad) - Math.sin(rollRad) * Math.cos(yawRad)) * a.z,
                (-Math.sin(pitchRad)) * a.x
                + (Math.cos(pitchRad) * Math.sin(rollRad)) * a.y
                + (Math.cos(pitchRad) * Math.cos(rollRad)) * a.z);
    }

    /**
     * @param rotationInRad in radians pitch, yaw, roll (same order as sent by GameBots)
     * @return
     */
    public static Triple rotationAsVector(Triple rotationInRad) {
        return rotateYawPitchRoll(new Triple(1, 0, 0), rotationInRad.y, rotationInRad.x, rotationInRad.z);
    }

    /**
     * @param rotationInRad in UTUnits (0..65535) pitch, yaw, roll (same order as sent by GameBots)
     * @return
     */
    public static Triple rotationAsVectorUTUnits(Triple rotationInUTUnits) {
        return rotateYawPitchRoll(new Triple(1, 0, 0), utAngleToRad(rotationInUTUnits.y), utAngleToRad(rotationInUTUnits.x), utAngleToRad(rotationInUTUnits.z));
    }

    public Triple inverse() {
        return new Triple(-x, -y, -z);
    }

    public static double multiScalar(Triple a, Triple b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    /**
     * Returns whether this == a (epsilon precision)
     * @param a
     * @param epsilon
     * @return
     */
    public boolean epsilonEquals(Triple a, double epsilon) {
        return Math.abs(this.x - a.x) < epsilon && Math.abs(this.y - a.y) < epsilon && Math.abs(this.z - a.z) < epsilon;
    }

    /**
     * Returns whether this == a (Triple.EPSILON precision)
     * @param a
     * @param epsilon
     * @return
     */
    public boolean epsilonEquals(Triple a) {
        return epsilonEquals(a, EPSILON);
    }

    /**
     * Returns angle in radians.
     * @param a
     * @param b
     * @return
     */
    public static double angle(Triple a, Triple b) {
        Triple normalizedA = new Triple(a).normalize();
        Triple normalizedB = new Triple(b).normalize();
        if (normalizedA.epsilonEquals(normalizedB)) {
            return 0;
        }
        if (normalizedA.epsilonEquals(normalizedB.inverse())) {
            return Math.PI;
        }
        return Math.acos((Triple.multiScalar(a, b)) / (a.vectorSize() * b.vectorSize()));
    }

    /**
     * Degrees to radians.
     * @param deg
     * @return
     */
    public static double degToRad(double deg) {
        return deg / 180 * Math.PI;
    }

    /**
     * Radians to degrees.
     * @param rad
     * @return
     */
    public static double radToDeg(double rad) {
        return rad / Math.PI * 180;
    }

    public static Triple locationToTriple(Location l) {
        return (l == null ? null : new Triple(l.x, l.y, l.z));
    }

    public static Triple rotationToTriple(Rotation r) {
        return (r == null ? null : new Triple(r.pitch, r.yaw, r.roll));
    }

    public Location getLocation() {
        return new Location(this.x, this.y, this.z);
    }

    public static Vector3d tripleToVector3d(Triple t) {
        return (t == null ? null : new Vector3d(t.x, t.y, t.z));
    }

    public Vector3d getVector3d() {
        return tripleToVector3d(this);
    }

    public static Triple velocityToTriple(Velocity v) {
        return new Triple(v.x, v.y, v.z);
    }
    
    public static Triple vector3dToTriple(Vector3d v) {
        return new Triple(v.x, v.y, v.z);
    }

    public Location add(Location loc) {
        return new Location(x + loc.x, y + loc.y, z + loc.z);
    }

    public Location add(Velocity vel) {
        return new Location(x + vel.x, y + vel.y, z + vel.z);
    }
}
