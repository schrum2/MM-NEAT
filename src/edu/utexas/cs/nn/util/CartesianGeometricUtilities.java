package edu.utexas.cs.nn.util;

import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class CartesianGeometricUtilities {

    public static double shortestDistanceToLineSegment(ILocated2D p1, ILocated2D l1, ILocated2D l2) {
        return shortestDistanceToLineSegment(p1.getX(), p1.getY(), l1.getX(), l2.getX(), l1.getY(), l2.getY());
    }

    public static double shortestDistanceToLineSegment(double x, double y, double x1, double x2, double y1, double y2) {
        double A = x - x1;
        double B = y - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = dot / len_sq;

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        ILocated2D other = new Tuple2D(xx, yy);
        return (new Tuple2D(x, y).distance(other));
    }

    public static double euclideanDistance(ArrayList<Double> x1, ArrayList<Double> x2) {
        double sum = 0;
        for (int i = 0; i < x1.size(); i++) {
            sum += Math.pow(x1.get(i) - x2.get(i), 2);
        }
        return Math.sqrt(sum);
    }

    public static double[] polarToCartesian(double r, double theta) {
        double x = r * Math.cos(theta);
        double y = r * Math.sin(theta);
        return new double[]{x, y};
    }

    // Leftover from PTSP
//    public static double signedAngleFromSourceHeadingToTarget(Vector2d source, Vector2d target, Vector2d dir) {
//        return signedAngleFromSourceHeadingToTarget(new Tuple2D(source.x, source.y), new Tuple2D(target.x, target.y), restrictRadians(dir.theta()));
//    }

    /**
     * Assuming that something at source is aimed towards the position at
     * sourceRads radians on the unit circle, compare the resulting vector to a
     * vector from the source to the target. The difference in radians between
     * the angles of the two vectors is returned, with different signs
     * indicating which side of each other the two vectors are.
     *
     * @param source any 2d located object
     * @param target any 2d located object
     * @param sourceRads between 0 and 2pi
     * @return difference in angles
     */
    public static double signedAngleFromSourceHeadingToTarget(ILocated2D source, ILocated2D target, double sourceRads) {
        if (target == null || target.getPosition() == null || source == null || source.getPosition() == null) {
            return Math.PI;
        }
        Tuple2D sourceToTarget = target.getPosition().sub(source);
        if (sourceToTarget.isZero()) {
            return 0;
        }
        sourceToTarget = sourceToTarget.normalize();
        double angleToTarget = sourceToTarget.angle();
        return signedAngleDifference(sourceRads, angleToTarget);
    }

    public static double signedAngleDifference(ILocated2D v1, ILocated2D v2) {
        return signedAngleDifference(v1.getPosition().angle(), v2.getPosition().angle());
    }

    public static double signedAngleDifference(double rad1, double rad2) {
        double angleDifference = rad1 - rad2;
        if (angleDifference > Math.PI) {
            angleDifference -= 2 * Math.PI;
        } else if (angleDifference < -Math.PI) {
            angleDifference += 2 * Math.PI;
        }
        return -angleDifference;
    }

    public static boolean sourceHeadingTowardsTarget(double sourceRadians, ILocated2D source, ILocated2D target, double allowance) {
        double angle = signedAngleFromSourceHeadingToTarget(source, target, sourceRadians);
        //System.out.println(source + "->" + sourceRadians + ":" + target + "=" + angle + " vs " + allowance);
        return Math.abs(angle) < allowance;
    }

    public static boolean onSideOf(ILocated2D source, double sourceRadians, ILocated2D other, boolean right) {
        double angle = signedAngleFromSourceHeadingToTarget(source, other, sourceRadians);
        return (right && (angle > 0)) || (!right && (angle < 0));
    }

    public static double restrictRadians(double rads) {
        while (rads >= 2 * Math.PI) {
            rads -= 2 * Math.PI;
        }
        while (rads < 0) {
            rads += 2 * Math.PI;
        }
        return rads;
    }
}
