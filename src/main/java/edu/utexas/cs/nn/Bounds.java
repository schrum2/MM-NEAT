package edu.utexas.cs.nn;

import java.io.Serializable;

/**
 * Cubic bound around a center
 * @author Igor Karpov (ikarpov@cs.utexas.edu)
 */
public class Bounds implements Serializable {
    private Point center;
    private double radius;

    public Bounds() {
        center = new Point();
        radius = 0;
    }

    public Bounds(Point center, double radius) {
        this.center = center; this.radius = radius;
    }

    public Bounds(double xmin, double ymin, double zmin,
           double xmax, double ymax, double zmax) {
        this.center = new Point((xmax + xmin)/2.0, (ymax + ymin)/2.0, (zmax + zmin)/2.0);
        double xrad = Math.abs((xmax - xmin)/2.0);
        double yrad = Math.abs((ymax - ymin)/2.0);
        double zrad = Math.abs((zmax - zmin)/2.0);
        this.radius = Math.max(xrad, Math.max(yrad, zrad));
    }

    public Point getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    /**
     * test if the bounds contain a point
     * @param p point to test
     * @return true iff p is within the r-cube around center
     */
    public boolean contains(Point p) {
        double dx = Math.abs(p.getX() - center.getX());
        double dy = Math.abs(p.getY() - center.getY());
        double dz = Math.abs(p.getZ() - center.getZ());
        return (dx <= radius) && (dy <= radius) && (dz <= radius);
    }

    public double getMinX() { return center.getX() - radius; }
    public double getMinY() { return center.getY() - radius; }
    public double getMinZ() { return center.getZ() - radius; }
    public double getMaxX() { return center.getX() + radius; }
    public double getMaxY() { return center.getY() + radius; }
    public double getMaxZ() { return center.getZ() + radius; }

    @Override
    public String toString() {
        return new String(this.center + "@" + this.radius);
    }
}
