package edu.utexas.cs.nn;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import mockcz.cuni.pogamut.MessageObjects.Triple;
import java.io.Serializable;

/**
 * A 3D point that's part of a timed path
 * @author Igor Karpov (ikarpov@cs.utexas.edu)
 */
public class Point implements Serializable, ILocated {
    private double t; // time
    private double x; // x-coord
    private double y; // y-coord
    private double z; // z-coord
    private double distance;
    Velocity velocity = null; // velocity (can be null)
    Rotation rotation = null; // rotation (can be null)
    Velocity acceleration = null; // acceleration (can be null)
    private int sequence = -1; // sequence
    private int index = -1; // index into the path
    private int previous_event_index = -1; // index to event before this point
    private int next_event_index = -1; // index to event after this point
    private int navpoint_id = -1; // id of the navpoint closest to this point

    public transient int code;

    public Point() {
        this(0,0,0,0);
    }

    public Point(AgentInfo info) {
        this.t = info.getTime();
        Location location = info.getLocation();
        if (location != null) {
            this.x = location.x;
            this.y = location.y;
            this.z = location.z;
        }
        this.velocity = info.getVelocity();
    }

    public Point(Location loc) {
        this.x = loc.x;
        this.y = loc.y;
        this.z = loc.z;
    }
    
    public Point(double time, Location loc, Rotation rot) {
        this.t = time;
        this.x = loc.x;
        this.y = loc.y;
        this.z = loc.z;
        this.rotation = rot;
    }

    public Point(double time, Location loc, Rotation rot, Velocity velocity) {
        this.t = time;
        this.x = loc.x;
        this.y = loc.y;
        this.z = loc.z;
        this.rotation = rot;
        this.velocity = velocity;
    }

    public Point(double time, Location loc, Rotation rot, Velocity velocity, Velocity acceleration) {
        this.t = time;
        this.x = loc.x;
        this.y = loc.y;
        this.z = loc.z;
        this.rotation = rot;
        this.velocity = velocity;
        this.acceleration = acceleration;
    }

    /** Parse a pose string */
    public static Point parsePose(String str) {
        String[] parts = str.split(" ");
        double time = 0;
        Location l = null;
        Rotation r = null;
        Velocity v = null;
        Velocity a = null;
        int navpoint_id = -1;
        if (parts.length >= 7) {
            time = Double.valueOf(parts[0]);
            l = new Location(Double.valueOf(parts[1]),Double.valueOf(parts[2]),Double.valueOf(parts[3]));
            r = new Rotation(Double.valueOf(parts[4]),Double.valueOf(parts[5]),Double.valueOf(parts[6]));
        }
        if(parts.length == 8)
        {
            navpoint_id = Integer.valueOf(parts[7]);
        }
        if(parts.length >= 13) {
            v = new Velocity(Double.valueOf(parts[7]),Double.valueOf(parts[8]),Double.valueOf(parts[9]));
            a = new Velocity(Double.valueOf(parts[10]),Double.valueOf(parts[11]),Double.valueOf(parts[12]));
        }
        if(parts.length == 14)
        {
            navpoint_id = Integer.valueOf(parts[13]);
        }
        Point p = new Point(time, l, r, v, a);
        p.setNavpointId(navpoint_id);
        return p;
    }

    public Location getLocation() {
        return new Location(x,y,z);
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public Velocity getVelocity() {
        return this.velocity;
    }

    public Velocity getAcceleration() {
        return this.acceleration;
    }

    /**
     * Get the distance from beginning of the sequence
     * @return path distance from beginning of the sequence
     */
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Point(double x, double y, double z) {
        this(x,y,z,0);
    }

    public Point(double x, double y, double z, double t) {
        this.x = x; this.y = y; this.z = z;
        this.t = t;
        this.index = -1;
    }

    public Point(double x, double y, double z, int index, double t) {
        this.x = x; this.y = y; this.z = z;
        this.t = t;
        this.index = index;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getT() {
        return t;
    }

    public Triple asTriple() {
        return new Triple(x, y, z);
    }

    public double[] asArray() {
        return new double[] {x,y,z};
    }

    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndexBefore() { return this.previous_event_index; }
    public int getIndexAfter() { return this.next_event_index; }
    public void setIndexBefore(int index) { this.previous_event_index = index; }
    public void setIndexAfter(int index) { this.next_event_index = index; }

    public int getNavpointId() { return this.navpoint_id; }
    public void setNavpointId(int navpoint_id) { this.navpoint_id = navpoint_id; }

    /**
     * Multiply by a scalar
     * @param s scalar to multiply by
     * @return result of multiplication (new point)
     */
    public Point times(double s) {
        return new Point(x * s, y * s, z * s);
    }

    /**
     * Add a point to a point
     * @param p point to add
     * @return result of addition (new point)
     */
    public Point plus(Point p) {
        return new Point(this.x + p.x, this.y + p.y, this.z + p.z);
    }

    /**
     * Subtract a point from this point
     * @param p point to add
     * @return result of addition (new point)
     */
    Point minus(Point p) {
        return new Point(this.x - p.x, this.y - p.y, this.z - p.z);
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")@(t:" + this.t + ", i:" + this.index + " s:" + this.sequence + ")";
    }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setZ(double z) { this.z = z; }
    public void setT(double t) { this.t = t; }
    public int getSequence() { return this.sequence; }
    public void setSequence(int sequence) { this.sequence = sequence; }

    public void setVelocity(Velocity velocity) { this.velocity = velocity; }

    public boolean isStartOfJump() {
        return acceleration != null && acceleration.z > 0.40 || velocity != null && velocity.z > 200;
    }

    public static Point[] boundsOffsetTable = {
        new Point(-0.5, -0.5, -0.5),
        new Point(+0.5, -0.5, -0.5),
        new Point(-0.5, +0.5, -0.5),
        new Point(+0.5, +0.5, -0.5),
        new Point(-0.5, -0.5, +0.5),
        new Point(+0.5, -0.5, +0.5),
        new Point(-0.5, +0.5, +0.5),
        new Point(+0.5, +0.5, +0.5)
    };

}
